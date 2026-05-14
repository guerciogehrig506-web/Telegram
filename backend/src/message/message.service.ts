import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { FirebaseService } from '../firebase/firebase.service';
import { CreateMessageDto } from './dto';

@Injectable()
export class MessageService {
  constructor(
    private prisma: PrismaService,
    private firebase: FirebaseService,
  ) {}

  async createMessage(createMessageDto: CreateMessageDto, senderId: string) {
    const message = await this.prisma.message.create({
      data: {
        content: createMessageDto.content,
        image: createMessageDto.image || null,
        type: createMessageDto.image ? 'image' : 'text',
        senderId,
        receiverId: createMessageDto.receiverId,
        groupId: createMessageDto.groupId,
      },
      include: {
        sender: true,
        receiver: true,
        group: true,
      },
    });

    this.sendPushForMessage(message);
    this.firebase.syncMessage(message).catch(() => {});

    return message;
  }

  private async sendPushForMessage(message: any) {
    try {
      const senderName = message.sender?.username || 'Someone';

      if (message.receiverId) {
        const receiver = await this.prisma.user.findUnique({
          where: { id: message.receiverId },
          select: { fcmToken: true, username: true },
        });
        if (receiver?.fcmToken) {
          this.firebase.sendPushNotification(
            receiver.fcmToken,
            senderName,
            message.type === 'image' ? '[Image]' : message.content,
            {
              type: 'message',
              senderId: message.senderId,
              messageId: message.id,
            },
          );
        }
      }

      if (message.groupId) {
        const members = await this.prisma.groupMember.findMany({
          where: { groupId: message.groupId, userId: { not: message.senderId } },
          include: { user: { select: { fcmToken: true } } },
        });
        const tokens = members
          .map((m) => m.user.fcmToken)
          .filter((t): t is string => !!t);

        if (tokens.length > 0) {
          this.firebase.sendPushToMultiple(
            tokens,
            `[${message.group?.name || 'Group'}] ${senderName}`,
            message.type === 'image' ? '[Image]' : message.content,
            {
              type: 'group_message',
              groupId: message.groupId,
              messageId: message.id,
            },
          );
        }
      }
    } catch (_) {}
  }

  async getMessagesBetweenUsers(userId: string, otherUserId: string) {
    return this.prisma.message.findMany({
      where: {
        OR: [
          { senderId: userId, receiverId: otherUserId },
          { senderId: otherUserId, receiverId: userId },
        ],
      },
      include: { sender: true, receiver: true },
      orderBy: { createdAt: 'asc' },
    });
  }

  async getGroupMessages(groupId: string) {
    return this.prisma.message.findMany({
      where: { groupId },
      include: { sender: true, group: true },
      orderBy: { createdAt: 'asc' },
    });
  }

  async markAsRead(messageId: string) {
    return this.prisma.message.update({
      where: { id: messageId },
      data: { isRead: true },
    });
  }

  async markMessagesAsRead(messageIds: string[]) {
    return this.prisma.message.updateMany({
      where: { id: { in: messageIds } },
      data: { isRead: true },
    });
  }

  async getAllMessages() {
    return this.prisma.message.findMany({
      include: { sender: true, receiver: true, group: true },
      orderBy: { createdAt: 'desc' },
    });
  }

  async getChats(userId: string) {
    const messages = await this.prisma.message.findMany({
      where: {
        OR: [
          { senderId: userId },
          { receiverId: userId },
        ],
        receiverId: { not: null },
      },
      include: {
        sender: { select: { id: true, username: true, avatar: true, department: true, lastSeen: true } },
        receiver: { select: { id: true, username: true, avatar: true, department: true, lastSeen: true } },
      },
      orderBy: { createdAt: 'desc' },
    });

    const unreadCounts = await this.prisma.message.groupBy({
      by: ['senderId'],
      where: {
        receiverId: userId,
        isRead: false,
      },
      _count: { id: true },
    });

    const unreadMap = new Map<string, number>();
    for (const item of unreadCounts) {
      unreadMap.set(item.senderId, item._count.id);
    }

    const chatMap = new Map<string, any>();
    for (const msg of messages) {
      const partner = msg.senderId === userId ? msg.receiver : msg.sender;
      if (!partner || chatMap.has(partner.id)) continue;

      chatMap.set(partner.id, {
        user: partner,
        lastMessage: msg.content,
        lastMessageTime: msg.createdAt.toISOString(),
        unreadCount: unreadMap.get(partner.id) || 0,
      });
    }

    return Array.from(chatMap.values());
  }

  async getUserGroups(userId: string) {
    return this.prisma.group.findMany({
      where: {
        members: {
          some: {
            userId,
          },
        },
      },
    });
  }

  async isGroupMember(userId: string, groupId: string) {
    const member = await this.prisma.groupMember.findUnique({
      where: {
        userId_groupId: {
          userId,
          groupId,
        },
      },
    });
    return !!member;
  }
}
