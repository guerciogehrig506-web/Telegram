import { WebSocketGateway, WebSocketServer, SubscribeMessage, OnGatewayConnection, OnGatewayDisconnect } from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { MessageService } from './message.service';

@WebSocketGateway({
  cors: { origin: '*' },
})
export class MessageGateway implements OnGatewayConnection, OnGatewayDisconnect {
  @WebSocketServer()
  server: Server;

  private connectedUsers: Map<string, string> = new Map();
  private userGroups: Map<string, Set<string>> = new Map();

  constructor(private messageService: MessageService) {}

  private async joinGroupRooms(client: Socket, userId: string) {
    try {
      const groups = await this.messageService.getUserGroups(userId);
      const groupIds = groups.map((g) => g.id);
      this.userGroups.set(userId, new Set(groupIds));
      groupIds.forEach((groupId) => {
        client.join(groupId);
        console.log(`User ${userId} joined group room: ${groupId}`);
      });
    } catch (error) {
      console.error(`Failed to join group rooms for user ${userId}:`, error);
    }
  }

  private leaveGroupRooms(client: Socket, userId: string) {
    const groupIds = this.userGroups.get(userId);
    if (groupIds) {
      groupIds.forEach((groupId) => {
        client.leave(groupId);
        console.log(`User ${userId} left group room: ${groupId}`);
      });
      this.userGroups.delete(userId);
    }
  }

  handleConnection(client: Socket) {
    console.log(`Client connected: ${client.id}`);
  }

  handleDisconnect(client: Socket) {
    const userId = this.connectedUsers.get(client.id);
    if (userId) {
      this.connectedUsers.delete(client.id);
      this.leaveGroupRooms(client, userId);

      setTimeout(async () => {
        const stillConnected = Array.from(this.connectedUsers.values()).includes(userId);
        if (!stillConnected) {
          this.server.emit('userOffline', { userId });
          console.log(`User ${userId} is now offline`);
        }
      }, 10000);
    }
    console.log(`Client disconnected: ${client.id}`);
  }

  @SubscribeMessage('authenticate')
  async handleAuthenticate(client: Socket, payload: { userId: string }) {
    const wasOffline = !Array.from(this.connectedUsers.values()).includes(payload.userId);
    this.connectedUsers.set(client.id, payload.userId);
    client.join(payload.userId);

    await this.joinGroupRooms(client, payload.userId);

    if (wasOffline) {
      this.server.emit('userOnline', { userId: payload.userId });
    }

    console.log(`User ${payload.userId} authenticated`);
    return { success: true };
  }

  @SubscribeMessage('sendMessage')
  async handleSendMessage(
    client: Socket,
    payload: { content: string; image?: string; senderId: string; receiverId?: string; groupId?: string },
  ) {
    const message = await this.messageService.createMessage(
      { content: payload.content, image: payload.image, receiverId: payload.receiverId, groupId: payload.groupId },
      payload.senderId,
    );

    if (payload.receiverId) {
      const receiverSocket = Array.from(this.connectedUsers.entries()).find(
        ([, userId]) => userId === payload.receiverId,
      );
      if (receiverSocket) {
        this.server.to(receiverSocket[0]).emit('newMessage', message);
      }
      const senderSocket = Array.from(this.connectedUsers.entries()).find(
        ([, userId]) => userId === payload.senderId,
      );
      if (senderSocket) {
        this.server.to(senderSocket[0]).emit('newMessage', message);
      }
    } else if (payload.groupId) {
      this.server.to(payload.groupId).emit('newMessage', message);
    }

    return message;
  }

  @SubscribeMessage('markAsRead')
  async handleMarkAsRead(client: Socket, payload: { messageIds: string[]; senderId: string }) {
    await this.messageService.markMessagesAsRead(payload.messageIds);

    const senderSocket = Array.from(this.connectedUsers.entries()).find(
      ([, userId]) => userId === payload.senderId,
    );
    if (senderSocket) {
      this.server.to(senderSocket[0]).emit('messagesRead', {
        messageIds: payload.messageIds,
        readBy: this.connectedUsers.get(client.id),
      });
    }

    return { success: true };
  }

  @SubscribeMessage('typing')
  handleTyping(client: Socket, payload: { userId: string; receiverId: string }) {
    const receiverSocket = Array.from(this.connectedUsers.entries()).find(
      ([, userId]) => userId === payload.receiverId,
    );
    if (receiverSocket) {
      this.server.to(receiverSocket[0]).emit('typing', { userId: payload.userId });
    }
  }

  @SubscribeMessage('stopTyping')
  handleStopTyping(client: Socket, payload: { userId: string; receiverId: string }) {
    const receiverSocket = Array.from(this.connectedUsers.entries()).find(
      ([, userId]) => userId === payload.receiverId,
    );
    if (receiverSocket) {
      this.server.to(receiverSocket[0]).emit('stopTyping', { userId: payload.userId });
    }
  }

  @SubscribeMessage('joinGroup')
  async handleJoinGroup(client: Socket, payload: { userId: string; groupId: string }) {
    const { userId, groupId } = payload;

    const isMember = await this.messageService.isGroupMember(userId, groupId);
    if (!isMember) {
      return { success: false, error: 'User is not a member of this group' };
    }

    client.join(groupId);

    const userGroups = this.userGroups.get(userId) || new Set();
    userGroups.add(groupId);
    this.userGroups.set(userId, userGroups);

    console.log(`User ${userId} joined group: ${groupId}`);
    return { success: true };
  }

  @SubscribeMessage('leaveGroup')
  handleLeaveGroup(client: Socket, payload: { userId: string; groupId: string }) {
    const { userId, groupId } = payload;

    client.leave(groupId);

    const userGroups = this.userGroups.get(userId);
    if (userGroups) {
      userGroups.delete(groupId);
      if (userGroups.size === 0) {
        this.userGroups.delete(userId);
      }
    }

    console.log(`User ${userId} left group: ${groupId}`);
    return { success: true };
  }
}