import { UsersService } from './users.service';
import { UpdateUserDto } from './dto/update-user.dto';
import { Request } from 'express';
export declare class UsersController {
    private usersService;
    constructor(usersService: UsersService);
    getMe(req: Request): Promise<{
        id: string;
        username: string;
        email: string;
        role: string;
        isActive: boolean;
        department: string;
        avatar: string;
        lastSeen: Date;
        createdAt: Date;
    }>;
    updateMe(req: Request, dto: UpdateUserDto): Promise<{
        id: string;
        username: string;
        email: string;
        role: string;
        isActive: boolean;
        department: string;
        avatar: string;
        lastSeen: Date;
        createdAt: Date;
    }>;
    updateFcmToken(req: Request, body: {
        token: string;
    }): Promise<{
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
    }>;
    findAll(req: Request, search?: string, department?: string): Promise<{
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
    }[]>;
    findById(id: string): Promise<{
        id: string;
        username: string;
        email: string;
        role: string;
        isActive: boolean;
        department: string;
        avatar: string;
        lastSeen: Date;
        createdAt: Date;
    }>;
}
