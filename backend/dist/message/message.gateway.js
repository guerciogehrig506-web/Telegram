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
exports.MessageGateway = void 0;
const websockets_1 = require("@nestjs/websockets");
const socket_io_1 = require("socket.io");
const message_service_1 = require("./message.service");
let MessageGateway = class MessageGateway {
    constructor(messageService) {
        this.messageService = messageService;
        this.connectedUsers = new Map();
        this.userGroups = new Map();
    }
    async joinGroupRooms(client, userId) {
        try {
            const groups = await this.messageService.getUserGroups(userId);
            const groupIds = groups.map((g) => g.id);
            this.userGroups.set(userId, new Set(groupIds));
            groupIds.forEach((groupId) => {
                client.join(groupId);
                console.log(`User ${userId} joined group room: ${groupId}`);
            });
        }
        catch (error) {
            console.error(`Failed to join group rooms for user ${userId}:`, error);
        }
    }
    leaveGroupRooms(client, userId) {
        const groupIds = this.userGroups.get(userId);
        if (groupIds) {
            groupIds.forEach((groupId) => {
                client.leave(groupId);
                console.log(`User ${userId} left group room: ${groupId}`);
            });
            this.userGroups.delete(userId);
        }
    }
    handleConnection(client) {
        console.log(`Client connected: ${client.id}`);
    }
    handleDisconnect(client) {
        const userId = this.connectedUsers.get(client.id);
        if (userId) {
            this.connectedUsers.delete(client.id);
            this.leaveGroupRooms(client, userId);
            setTimeout(async () => {
                const stillConnected = Array.from(this.connectedUsers.values()).includes(userId);
                if (!stillConnected) {
                    this.server.emit('userOffline', { userId });
                    console.log(`User ${userId} is now offline`);
                }
            }, 10000);
        }
        console.log(`Client disconnected: ${client.id}`);
    }
    async handleAuthenticate(client, payload) {
        const wasOffline = !Array.from(this.connectedUsers.values()).includes(payload.userId);
        this.connectedUsers.set(client.id, payload.userId);
        client.join(payload.userId);
        await this.joinGroupRooms(client, payload.userId);
        if (wasOffline) {
            this.server.emit('userOnline', { userId: payload.userId });
        }
        console.log(`User ${payload.userId} authenticated`);
        return { success: true };
    }
    async handleSendMessage(client, payload) {
        const message = await this.messageService.createMessage({ content: payload.content, image: payload.image, receiverId: payload.receiverId, groupId: payload.groupId }, payload.senderId);
        if (payload.receiverId) {
            const receiverSocket = Array.from(this.connectedUsers.entries()).find(([, userId]) => userId === payload.receiverId);
            if (receiverSocket) {
                this.server.to(receiverSocket[0]).emit('newMessage', message);
            }
            const senderSocket = Array.from(this.connectedUsers.entries()).find(([, userId]) => userId === payload.senderId);
            if (senderSocket) {
                this.server.to(senderSocket[0]).emit('newMessage', message);
            }
        }
        else if (payload.groupId) {
            this.server.to(payload.groupId).emit('newMessage', message);
        }
        return message;
    }
    async handleMarkAsRead(client, payload) {
        await this.messageService.markMessagesAsRead(payload.messageIds);
        const senderSocket = Array.from(this.connectedUsers.entries()).find(([, userId]) => userId === payload.senderId);
        if (senderSocket) {
            this.server.to(senderSocket[0]).emit('messagesRead', {
                messageIds: payload.messageIds,
                readBy: this.connectedUsers.get(client.id),
            });
        }
        return { success: true };
    }
    handleTyping(client, payload) {
        const receiverSocket = Array.from(this.connectedUsers.entries()).find(([, userId]) => userId === payload.receiverId);
        if (receiverSocket) {
            this.server.to(receiverSocket[0]).emit('typing', { userId: payload.userId });
        }
    }
    handleStopTyping(client, payload) {
        const receiverSocket = Array.from(this.connectedUsers.entries()).find(([, userId]) => userId === payload.receiverId);
        if (receiverSocket) {
            this.server.to(receiverSocket[0]).emit('stopTyping', { userId: payload.userId });
        }
    }
    async handleJoinGroup(client, payload) {
        const { userId, groupId } = payload;
        const isMember = await this.messageService.isGroupMember(userId, groupId);
        if (!isMember) {
            return { success: false, error: 'User is not a member of this group' };
        }
        client.join(groupId);
        const userGroups = this.userGroups.get(userId) || new Set();
        userGroups.add(groupId);
        this.userGroups.set(userId, userGroups);
        console.log(`User ${userId} joined group: ${groupId}`);
        return { success: true };
    }
    handleLeaveGroup(client, payload) {
        const { userId, groupId } = payload;
        client.leave(groupId);
        const userGroups = this.userGroups.get(userId);
        if (userGroups) {
            userGroups.delete(groupId);
            if (userGroups.size === 0) {
                this.userGroups.delete(userId);
            }
        }
        console.log(`User ${userId} left group: ${groupId}`);
        return { success: true };
    }
};
exports.MessageGateway = MessageGateway;
__decorate([
    (0, websockets_1.WebSocketServer)(),
    __metadata("design:type", socket_io_1.Server)
], MessageGateway.prototype, "server", void 0);
__decorate([
    (0, websockets_1.SubscribeMessage)('authenticate'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [socket_io_1.Socket, Object]),
    __metadata("design:returntype", Promise)
], MessageGateway.prototype, "handleAuthenticate", null);
__decorate([
    (0, websockets_1.SubscribeMessage)('sendMessage'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [socket_io_1.Socket, Object]),
    __metadata("design:returntype", Promise)
], MessageGateway.prototype, "handleSendMessage", null);
__decorate([
    (0, websockets_1.SubscribeMessage)('markAsRead'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [socket_io_1.Socket, Object]),
    __metadata("design:returntype", Promise)
], MessageGateway.prototype, "handleMarkAsRead", null);
__decorate([
    (0, websockets_1.SubscribeMessage)('typing'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [socket_io_1.Socket, Object]),
    __metadata("design:returntype", void 0)
], MessageGateway.prototype, "handleTyping", null);
__decorate([
    (0, websockets_1.SubscribeMessage)('stopTyping'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [socket_io_1.Socket, Object]),
    __metadata("design:returntype", void 0)
], MessageGateway.prototype, "handleStopTyping", null);
__decorate([
    (0, websockets_1.SubscribeMessage)('joinGroup'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [socket_io_1.Socket, Object]),
    __metadata("design:returntype", Promise)
], MessageGateway.prototype, "handleJoinGroup", null);
__decorate([
    (0, websockets_1.SubscribeMessage)('leaveGroup'),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [socket_io_1.Socket, Object]),
    __metadata("design:returntype", void 0)
], MessageGateway.prototype, "handleLeaveGroup", null);
exports.MessageGateway = MessageGateway = __decorate([
    (0, websockets_1.WebSocketGateway)({
        cors: { origin: '*' },
    }),
    __metadata("design:paramtypes", [message_service_1.MessageService])
], MessageGateway);
//# sourceMappingURL=message.gateway.js.map