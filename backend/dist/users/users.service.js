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
exports.UsersService = void 0;
const common_1 = require("@nestjs/common");
const prisma_service_1 = require("../prisma/prisma.service");
let UsersService = class UsersService {
    constructor(prisma) {
        this.prisma = prisma;
    }
    async findAll(excludeUserId, search, department) {
        const where = {};
        if (excludeUserId)
            where.id = { not: excludeUserId };
        if (search) {
            where.OR = [
                { username: { contains: search } },
                { email: { contains: search } },
            ];
        }
        if (department)
            where.department = department;
        return this.prisma.user.findMany({
            where,
            select: {
                id: true,
                username: true,
                email: true,
                role: true,
                department: true,
                bio: true,
                avatar: true,
                lastSeen: true,
                isActive: true,
                createdAt: true,
            },
            orderBy: { username: 'asc' },
        });
    }
    async findById(id) {
        return this.prisma.user.findUnique({
            where: { id },
            select: {
                id: true,
                username: true,
                email: true,
                role: true,
                department: true,
                avatar: true,
                lastSeen: true,
                isActive: true,
                createdAt: true,
            },
        });
    }
    async getMe(userId) {
        return this.findById(userId);
    }
    async updateMe(userId, dto) {
        return this.prisma.user.update({
            where: { id: userId },
            data: {
                ...(dto.username !== undefined && { username: dto.username }),
                ...(dto.bio !== undefined && { bio: dto.bio }),
                ...(dto.avatar !== undefined && { avatar: dto.avatar }),
            },
            select: {
                id: true,
                username: true,
                email: true,
                role: true,
                department: true,
                avatar: true,
                lastSeen: true,
                isActive: true,
                createdAt: true,
            },
        });
    }
    async updateFcmToken(userId, token) {
        return this.prisma.user.update({
            where: { id: userId },
            data: { fcmToken: token },
        });
    }
};
exports.UsersService = UsersService;
exports.UsersService = UsersService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [prisma_service_1.PrismaService])
], UsersService);
//# sourceMappingURL=users.service.js.map