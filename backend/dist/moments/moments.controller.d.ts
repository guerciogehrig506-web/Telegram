import { MomentsService } from './moments.service';
import { Request } from 'express';
export declare class MomentsController {
    private momentsService;
    constructor(momentsService: MomentsService);
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
    create(req: Request, body: {
        content: string;
    }): Promise<{
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
    toggleLike(id: string, req: Request): Promise<{
        liked: boolean;
    }>;
}
