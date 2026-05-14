import { PrismaService } from '../prisma/prisma.service';
import { FirebaseService } from '../firebase/firebase.service';
import { CreateMessageDto } from './dto';
export declare class MessageService {
    private prisma;
    private firebase;
    constructor(prisma: PrismaService, firebase: FirebaseService);
    createMessage(createMessageDto: CreateMessageDto, senderId: string): Promise<{
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
    private sendPushForMessage;
    getMessagesBetweenUsers(userId: string, otherUserId: string): Promise<({
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
    markAsRead(messageId: string): Promise<{
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
    markMessagesAsRead(messageIds: string[]): Promise<import(".prisma/client").Prisma.BatchPayload>;
    getAllMessages(): Promise<({
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
    })[]>;
    getChats(userId: string): Promise<any[]>;
    getUserGroups(userId: string): Promise<{
        id: string;
        avatar: string;
        createdAt: Date;
        updatedAt: Date;
        name: string;
        creatorId: string;
    }[]>;
    isGroupMember(userId: string, groupId: string): Promise<boolean>;
}
