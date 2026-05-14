import { MessageService } from './message.service';
import { CreateMessageDto } from './dto';
import { Request } from 'express';
export declare class MessageController {
    private messageService;
    constructor(messageService: MessageService);
    create(createMessageDto: CreateMessageDto, req: Request): Promise<{
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
    getChats(req: Request): Promise<any[]>;
    getMessagesBetweenUsers(req: Request, otherUserId: string): Promise<({
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
    })[]>;
    getGroupMessages(groupId: string): Promise<({
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
    })[]>;
}
