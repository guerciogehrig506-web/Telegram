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
exports.MessageService = void 0;
const common_1 = require("@nestjs/common");
const prisma_service_1 = require("../prisma/prisma.service");
const firebase_service_1 = require("../firebase/firebase.service");
let MessageService = class MessageService {
    constructor(prisma, firebase) {
        this.prisma = prisma;
        this.firebase = firebase;
    }
    async createMessage(createMessageDto, senderId) {
        const message = await this.prisma.message.create({
            data: {
                content: createMessageDto.content,
                image: createMessageDto.image || null,
                type: createMessageDto.image ? 'image' : 'text',
                senderId,
                receiverId: createMessageDto.receiverId,
                groupId: createMessageDto.groupId,
            },
            include: {
                sender: true,
                receiver: true,
                group: true,
            },
        });
        this.sendPushForMessage(message);
        this.firebase.syncMessage(message).catch(() => { });
        return message;
    }
    async sendPushForMessage(message) {
        try {
            const senderName = message.sender?.username || 'Someone';
            if (message.receiverId) {
                const receiver = await this.prisma.user.findUnique({
                    where: { id: message.receiverId },
                    select: { fcmToken: true, username: true },
                });
                if (receiver?.fcmToken) {
                    this.firebase.sendPushNotification(receiver.fcmToken, senderName, message.type === 'image' ? '[Image]' : message.content, {
                        type: 'message',
                        senderId: message.senderId,
                        messageId: message.id,
                    });
                }
            }
            if (message.groupId) {
                const members = await this.prisma.groupMember.findMany({
                    where: { groupId: message.groupId, userId: { not: message.senderId } },
                    include: { user: { select: { fcmToken: true } } },
                });
                const tokens = members
                    .map((m) => m.user.fcmToken)
                    .filter((t) => !!t);
                if (tokens.length > 0) {
                    this.firebase.sendPushToMultiple(tokens, `[${message.group?.name || 'Group'}] ${senderName}`, message.type === 'image' ? '[Image]' : message.content, {
                        type: 'group_message',
                        groupId: message.groupId,
                        messageId: message.id,
                    });
                }
            }
        }
        catch (_) { }
    }
    async getMessagesBetweenUsers(userId, otherUserId) {
        return this.prisma.message.findMany({
            where: {
                OR: [
                    { senderId: userId, receiverId: otherUserId },
                    { senderId: otherUserId, receiverId: userId },
                ],
            },
            include: { sender: true, receiver: true },
            orderBy: { createdAt: 'asc' },
        });
    }
    async getGroupMessages(groupId) {
        return this.prisma.message.findMany({
            where: { groupId },
            include: { sender: true, group: true },
            orderBy: { createdAt: 'asc' },
        });
    }
    async markAsRead(messageId) {
        return this.prisma.message.update({
            where: { id: messageId },
            data: { isRead: true },
        });
    }
    async markMessagesAsRead(messageIds) {
        return this.prisma.message.updateMany({
            where: { id: { in: messageIds } },
            data: { isRead: true },
        });
    }
    async getAllMessages() {
        return this.prisma.message.findMany({
            include: { sender: true, receiver: true, group: true },
            orderBy: { createdAt: 'desc' },
        });
    }
    async getChats(userId) {
        const messages = await this.prisma.message.findMany({
            where: {
                OR: [
                    { senderId: userId },
                    { receiverId: userId },
                ],
                receiverId: { not: null },
            },
            include: {
                sender: { select: { id: true, username: true, avatar: true, department: true, lastSeen: true } },
                receiver: { select: { id: true, username: true, avatar: true, department: true, lastSeen: true } },
            },
            orderBy: { createdAt: 'desc' },
        });
        const unreadCounts = await this.prisma.message.groupBy({
            by: ['senderId'],
            where: {
                receiverId: userId,
                isRead: false,
            },
            _count: { id: true },
        });
        const unreadMap = new Map();
        for (const item of unreadCounts) {
            unreadMap.set(item.senderId, item._count.id);
        }
        const chatMap = new Map();
        for (const msg of messages) {
            const partner = msg.senderId === userId ? msg.receiver : msg.sender;
            if (!partner || chatMap.has(partner.id))
                continue;
            chatMap.set(partner.id, {
                user: partner,
                lastMessage: msg.content,
                lastMessageTime: msg.createdAt.toISOString(),
                unreadCount: unreadMap.get(partner.id) || 0,
            });
        }
        return Array.from(chatMap.values());
    }
    async getUserGroups(userId) {
        return this.prisma.group.findMany({
            where: {
                members: {
                    some: {
                        userId,
                    },
                },
            },
        });
    }
    async isGroupMember(userId, groupId) {
        const member = await this.prisma.groupMember.findUnique({
            where: {
                userId_groupId: {
                    userId,
                    groupId,
                },
            },
        });
        return !!member;
    }
};
exports.MessageService = MessageService;
exports.MessageService = MessageService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [prisma_service_1.PrismaService,
        firebase_service_1.FirebaseService])
], MessageService);
//# sourceMappingURL=message.service.js.map