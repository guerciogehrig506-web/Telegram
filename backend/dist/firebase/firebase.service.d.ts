import { OnModuleInit } from '@nestjs/common';
import * as admin from 'firebase-admin';
export declare class FirebaseService implements OnModuleInit {
    private readonly logger;
    private app;
    onModuleInit(): void;
    sendPushNotification(token: string, title: string, body: string, data?: Record<string, string>): Promise<void>;
    sendPushToMultiple(tokens: string[], title: string, body: string, data?: Record<string, string>): Promise<void>;
    getStorageBucket(): import("@google-cloud/storage").Bucket;
    uploadToStorage(filePath: string, destination: string, contentType?: string): Promise<string | null>;
    deleteFromStorage(destination: string): Promise<void>;
    get firestore(): admin.firestore.Firestore;
    syncUser(user: {
        id: string;
        username: string;
        email: string;
        role: string;
        department?: string | null;
        bio?: string | null;
        avatar?: string | null;
        isActive: boolean;
        lastSeen?: Date | null;
        createdAt: Date;
    }): Promise<void>;
    syncMessage(message: {
        id: string;
        content: string;
        type: string;
        image?: string | null;
        senderId: string;
        receiverId?: string | null;
        groupId?: string | null;
        isRead: boolean;
        createdAt: Date;
        sender?: {
            id: string;
            username: string;
            avatar?: string | null;
        };
    }): Promise<void>;
    syncGroup(group: {
        id: string;
        name: string;
        avatar?: string | null;
        createdAt: Date;
        members?: string[];
    }): Promise<void>;
}
