import { PrismaService } from '../prisma/prisma.service';
import { UpdateUserDto } from './dto/update-user.dto';
export declare class UsersService {
    private prisma;
    constructor(prisma: PrismaService);
    findAll(excludeUserId?: string, search?: string, department?: string): Promise<{
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
    getMe(userId: string): Promise<{
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
    updateMe(userId: string, dto: UpdateUserDto): Promise<{
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
    updateFcmToken(userId: string, token: string): Promise<{
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
}
