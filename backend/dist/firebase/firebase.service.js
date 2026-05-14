"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var FirebaseService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.FirebaseService = void 0;
const common_1 = require("@nestjs/common");
const admin = require("firebase-admin");
const path_1 = require("path");
let FirebaseService = FirebaseService_1 = class FirebaseService {
    constructor() {
        this.logger = new common_1.Logger(FirebaseService_1.name);
        this.app = null;
    }
    onModuleInit() {
        try {
            const serviceAccountPath = (0, path_1.join)(__dirname, '..', '..', 'firebase-adminsdk.json');
            const fs = require('fs');
            if (fs.existsSync(serviceAccountPath)) {
                const serviceAccount = require(serviceAccountPath);
                this.app = admin.initializeApp({
                    credential: admin.credential.cert(serviceAccount),
                    storageBucket: `${serviceAccount.project_id}.appspot.com`,
                });
                this.logger.log('Firebase Admin SDK initialized successfully');
            }
            else {
                this.logger.warn('firebase-adminsdk.json not found, FCM push disabled');
                this.logger.warn('Place the service account key at: ' + serviceAccountPath);
            }
        }
        catch (error) {
            this.logger.warn('Firebase Admin SDK init failed, FCM push disabled: ' + error.message);
        }
    }
    async sendPushNotification(token, title, body, data) {
        if (!this.app)
            return;
        try {
            await this.app.messaging().send({
                token,
                notification: { title, body },
                data: data || {},
                android: {
                    priority: 'high',
                    notification: {
                        channelId: 'trio_messages',
                        sound: 'default',
                    },
                },
            });
        }
        catch (error) {
            this.logger.warn(`FCM push failed: ${error.message}`);
        }
    }
    async sendPushToMultiple(tokens, title, body, data) {
        if (!this.app || tokens.length === 0)
            return;
        try {
            await this.app.messaging().sendEachForMulticast({
                tokens,
                notification: { title, body },
                data: data || {},
                android: {
                    priority: 'high',
                    notification: {
                        channelId: 'trio_messages',
                        sound: 'default',
                    },
                },
            });
        }
        catch (error) {
            this.logger.warn(`FCM multicast push failed: ${error.message}`);
        }
    }
    getStorageBucket() {
        if (!this.app)
            return null;
        return this.app.storage().bucket();
    }
    async uploadToStorage(filePath, destination, contentType) {
        if (!this.app)
            return null;
        try {
            const bucket = this.app.storage().bucket();
            await bucket.upload(filePath, {
                destination,
                metadata: { contentType },
            });
            const file = bucket.file(destination);
            const [url] = await file.getSignedUrl({
                action: 'read',
                expires: Date.now() + 365 * 24 * 60 * 60 * 1000,
            });
            return url;
        }
        catch (error) {
            this.logger.warn(`Storage upload failed: ${error.message}`);
            return null;
        }
    }
    async deleteFromStorage(destination) {
        if (!this.app)
            return;
        try {
            const bucket = this.app.storage().bucket();
            await bucket.file(destination).delete();
        }
        catch (error) {
            this.logger.warn(`Storage delete failed: ${error.message}`);
        }
    }
    get firestore() {
        if (!this.app)
            return null;
        return this.app.firestore();
    }
    async syncUser(user) {
        try {
            const db = this.firestore;
            if (!db)
                return;
            await db.collection('users').doc(user.id).set({
                id: user.id,
                username: user.username,
                email: user.email,
                role: user.role,
                department: user.department || '',
                bio: user.bio || '',
                avatar: user.avatar || '',
                isActive: user.isActive,
                lastSeen: user.lastSeen?.getTime() || 0,
                createdAt: user.createdAt.getTime(),
                updatedAt: Date.now(),
            }, { merge: true });
        }
        catch (error) {
            this.logger.warn(`Firestore user sync failed: ${error.message}`);
        }
    }
    async syncMessage(message) {
        try {
            const db = this.firestore;
            if (!db)
                return;
            await db.collection('messages').doc(message.id).set({
                id: message.id,
                content: message.content,
                type: message.type,
                image: message.image || '',
                senderId: message.senderId,
                senderName: message.sender?.username || '',
                senderAvatar: message.sender?.avatar || '',
                receiverId: message.receiverId || null,
                groupId: message.groupId || null,
                isRead: message.isRead,
                createdAt: message.createdAt.getTime(),
            }, { merge: true });
        }
        catch (error) {
            this.logger.warn(`Firestore message sync failed: ${error.message}`);
        }
    }
    async syncGroup(group) {
        try {
            const db = this.firestore;
            if (!db)
                return;
            await db.collection('groups').doc(group.id).set({
                id: group.id,
                name: group.name,
                avatar: group.avatar || '',
                members: group.members || [],
                createdAt: group.createdAt.getTime(),
            }, { merge: true });
        }
        catch (error) {
            this.logger.warn(`Firestore group sync failed: ${error.message}`);
        }
    }
};
exports.FirebaseService = FirebaseService;
exports.FirebaseService = FirebaseService = FirebaseService_1 = __decorate([
    (0, common_1.Injectable)()
], FirebaseService);
//# sourceMappingURL=firebase.service.js.map