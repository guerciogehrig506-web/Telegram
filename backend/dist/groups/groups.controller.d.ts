import { GroupsService } from './groups.service';
import { CreateGroupDto, UpdateGroupDto, AddMemberDto } from './dto/group.dto';
import { Request } from 'express';
export declare class GroupsController {
    private groupsService;
    constructor(groupsService: GroupsService);
    create(req: Request, dto: CreateGroupDto): Promise<{
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
    findAll(req: Request): Promise<{
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
    addMembers(id: string, dto: AddMemberDto): Promise<{
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
