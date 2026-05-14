"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.GroupsService = void 0;
const common_1 = require("@nestjs/common");
const prisma_service_1 = require("../prisma/prisma.service");
const firebase_service_1 = require("../firebase/firebase.service");
const GROUP_SELECT = {
    id: true,
    name: true,
    avatar: true,
    createdAt: true,
    members: {
        select: {
            id: true,
            user: {
                select: {
                    id: true,
                    username: true,
                    avatar: true,
                    bio: true,
                    lastSeen: true,
                },
            },
        },
    },
};
let GroupsService = class GroupsService {
    constructor(prisma, firebase) {
        this.prisma = prisma;
        this.firebase = firebase;
    }
    async create(dto, creatorId) {
        const allMemberIds = [...new Set([creatorId, ...dto.memberIds])];
        const group = await this.prisma.group.create({
            data: {
                name: dto.name,
                avatar: dto.avatar || '',
                creatorId: creatorId,
                members: {
                    create: allMemberIds.map((userId) => ({ userId })),
                },
            },
            select: GROUP_SELECT,
        });
        this.firebase.syncGroup({
            id: group.id,
            name: group.name,
            avatar: group.avatar,
            createdAt: group.createdAt,
            members: allMemberIds,
        }).catch(() => { });
        return group;
    }
    async findAll(userId) {
        return this.prisma.group.findMany({
            where: {
                members: { some: { userId } },
            },
            select: GROUP_SELECT,
            orderBy: { createdAt: 'desc' },
        });
    }
    async findById(id) {
        return this.prisma.group.findUnique({
            where: { id },
            select: GROUP_SELECT,
        });
    }
    async update(id, dto) {
        return this.prisma.group.update({
            where: { id },
            data: {
                ...(dto.name !== undefined && { name: dto.name }),
                ...(dto.avatar !== undefined && { avatar: dto.avatar }),
            },
            select: GROUP_SELECT,
        });
    }
    async addMembers(id, userIds) {
        const group = await this.prisma.group.findUnique({ where: { id }, select: { id: true } });
        if (!group)
            return null;
        const existing = await this.prisma.groupMember.findMany({
            where: { groupId: id, userId: { in: userIds } },
            select: { userId: true },
        });
        const existingIds = new Set(existing.map((m) => m.userId));
        const newIds = userIds.filter((uid) => !existingIds.has(uid));
        if (newIds.length > 0) {
            await this.prisma.groupMember.createMany({
                data: newIds.map((userId) => ({ groupId: id, userId })),
            });
        }
        return this.findById(id);
    }
    async removeMember(id, userId) {
        await this.prisma.groupMember.deleteMany({
            where: { groupId: id, userId },
        });
        return this.findById(id);
    }
    async delete(id) {
        await this.prisma.group.delete({ where: { id } });
        return { success: true };
    }
};
exports.GroupsService = GroupsService;
exports.GroupsService = GroupsService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [prisma_service_1.PrismaService,
        firebase_service_1.FirebaseService])
], GroupsService);
//# sourceMappingURL=groups.service.js.map