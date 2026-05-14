import { Request, Response } from 'express';
import { PrismaService } from '../prisma/prisma.service';
export declare class AdminController {
    private prisma;
    constructor(prisma: PrismaService);
    getLogin(req: Request): {
        admin: {
            id: string;
            username: string;
            email: string;
        };
        error?: undefined;
    } | {
        error: any;
        admin?: undefined;
    };
    postLogin(email: string, password: string, req: Request, res: Response): Promise<void>;
    logout(req: Request, res: Response): Promise<void>;
    dashboard(req: Request): Promise<{
        admin: {
            id: string;
            username: string;
            email: string;
        };
        stats: {
            users: number;
            messages: number;
            moments: number;
            groups: number;
        };
    }>;
    users(req: Request): Promise<{
        admin: {
            id: string;
            username: string;
            email: string;
        };
        users: {
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
        }[];
    }>;
    messages(req: Request): Promise<{
        admin: {
            id: string;
            username: string;
            email: string;
        };
        messages: ({
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
        })[];
    }>;
    moments(req: Request): Promise<{
        admin: {
            id: string;
            username: string;
            email: string;
        };
        moments: ({
            user: {
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
            _count: {
                likes: number;
            };
        } & {
            id: string;
            createdAt: Date;
            updatedAt: Date;
            content: string;
            userId: string;
        })[];
    }>;
}
