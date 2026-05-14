import { PrismaService } from '../prisma/prisma.service';
export declare class MomentsService {
    private prisma;
    constructor(prisma: PrismaService);
    findAll(): Promise<({
        user: {
            id: string;
            username: string;
            department: string;
            avatar: string;
        };
        likes: {
            userId: string;
        }[];
    } & {
        id: string;
        createdAt: Date;
        updatedAt: Date;
        content: string;
        userId: string;
    })[]>;
    create(userId: string, content: string): Promise<{
        user: {
            id: string;
            username: string;
            department: string;
            avatar: string;
        };
        likes: {
            id: string;
            createdAt: Date;
            userId: string;
            momentId: string;
        }[];
    } & {
        id: string;
        createdAt: Date;
        updatedAt: Date;
        content: string;
        userId: string;
    }>;
    toggleLike(momentId: string, userId: string): Promise<{
        liked: boolean;
    }>;
}
