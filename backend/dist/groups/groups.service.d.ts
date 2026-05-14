import { PrismaService } from '../prisma/prisma.service';
import { FirebaseService } from '../firebase/firebase.service';
import { CreateGroupDto, UpdateGroupDto } from './dto/group.dto';
export declare class GroupsService {
    private prisma;
    private firebase;
    constructor(prisma: PrismaService, firebase: FirebaseService);
    create(dto: CreateGroupDto, creatorId: string): Promise<{
        id: string;
        avatar: string;
        createdAt: Date;
        name: string;
        members: {
            user: {
                id: string;
                username: string;
                bio: string;
                avatar: string;
                lastSeen: Date;
            };
            id: string;
        }[];
    }>;
    findAll(userId: string): Promise<{
        id: string;
        avatar: string;
        createdAt: Date;
        name: string;
        members: {
            user: {
                id: string;
                username: string;
                bio: string;
                avatar: string;
                lastSeen: Date;
            };
            id: string;
        }[];
    }[]>;
    findById(id: string): Promise<{
        id: string;
        avatar: string;
        createdAt: Date;
        name: string;
        members: {
            user: {
                id: string;
                username: string;
                bio: string;
                avatar: string;
                lastSeen: Date;
            };
            id: string;
        }[];
    }>;
    update(id: string, dto: UpdateGroupDto): Promise<{
        id: string;
        avatar: string;
        createdAt: Date;
        name: string;
        members: {
            user: {
                id: string;
                username: string;
                bio: string;
                avatar: string;
                lastSeen: Date;
            };
            id: string;
        }[];
    }>;
    addMembers(id: string, userIds: string[]): Promise<{
        id: string;
        avatar: string;
        createdAt: Date;
        name: string;
        members: {
            user: {
                id: string;
                username: string;
                bio: string;
                avatar: string;
                lastSeen: Date;
            };
            id: string;
        }[];
    }>;
    removeMember(id: string, userId: string): Promise<{
        id: string;
        avatar: string;
        createdAt: Date;
        name: string;
        members: {
            user: {
                id: string;
                username: string;
                bio: string;
                avatar: string;
                lastSeen: Date;
            };
            id: string;
        }[];
    }>;
    delete(id: string): Promise<{
        success: boolean;
    }>;
}
