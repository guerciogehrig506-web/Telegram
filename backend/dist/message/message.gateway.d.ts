import { OnGatewayConnection, OnGatewayDisconnect } from '@nestjs/websockets';
import { Server, Socket } from 'socket.io';
import { MessageService } from './message.service';
export declare class MessageGateway implements OnGatewayConnection, OnGatewayDisconnect {
    private messageService;
    server: Server;
    private connectedUsers;
    private userGroups;
    constructor(messageService: MessageService);
    private joinGroupRooms;
    private leaveGroupRooms;
    handleConnection(client: Socket): void;
    handleDisconnect(client: Socket): void;
    handleAuthenticate(client: Socket, payload: {
        userId: string;
    }): Promise<{
        success: boolean;
    }>;
    handleSendMessage(client: Socket, payload: {
        content: string;
        image?: string;
        senderId: string;
        receiverId?: string;
        groupId?: string;
    }): Promise<{
        group: {
            id: string;
            avatar: string;
            createdAt: Date;
            updatedAt: Date;
            name: string;
            creatorId: string;
        };
        sender: {
            id: string;
            username: string;
            email: string;
            role: string;
            isActive: boolean;
            department: string;
            bio: string;
            avatar: string;
            lastSeen: Date;
            createdAt: Date;
            updatedAt: Date;
            password: string;
            fcmToken: string | null;
        };
        receiver: {
            id: string;
            username: string;
            email: string;
            role: string;
            isActive: boolean;
            department: string;
            bio: string;
            avatar: string;
            lastSeen: Date;
            createdAt: Date;
            updatedAt: Date;
            password: string;
            fcmToken: string | null;
        };
    } & {
        id: string;
        createdAt: Date;
        updatedAt: Date;
        content: string;
        type: string;
        senderId: string;
        isRead: boolean;
        image: string | null;
        receiverId: string | null;
        groupId: string | null;
    }>;
    handleMarkAsRead(client: Socket, payload: {
        messageIds: string[];
        senderId: string;
    }): Promise<{
        success: boolean;
    }>;
    handleTyping(client: Socket, payload: {
        userId: string;
        receiverId: string;
    }): void;
    handleStopTyping(client: Socket, payload: {
        userId: string;
        receiverId: string;
    }): void;
    handleJoinGroup(client: Socket, payload: {
        userId: string;
        groupId: string;
    }): Promise<{
        success: boolean;
        error: string;
    } | {
        success: boolean;
        error?: undefined;
    }>;
    handleLeaveGroup(client: Socket, payload: {
        userId: string;
        groupId: string;
    }): {
        success: boolean;
    };
}
