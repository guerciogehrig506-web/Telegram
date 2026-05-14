import { Injectable, Logger, OnModuleInit } from '@nestjs/common';
import * as admin from 'firebase-admin';
import { join } from 'path';

@Injectable()
export class FirebaseService implements OnModuleInit {
  private readonly logger = new Logger(FirebaseService.name);
  private app: admin.app.App | null = null;

  onModuleInit() {
    try {
      const serviceAccountPath = join(__dirname, '..', '..', 'firebase-adminsdk.json');
      const fs = require('fs');

      if (fs.existsSync(serviceAccountPath)) {
        const serviceAccount = require(serviceAccountPath);
        this.app = admin.initializeApp({
          credential: admin.credential.cert(serviceAccount),
          storageBucket: `${serviceAccount.project_id}.appspot.com`,
        });
        this.logger.log('Firebase Admin SDK initialized successfully');
      } else {
        this.logger.warn('firebase-adminsdk.json not found, FCM push disabled');
        this.logger.warn('Place the service account key at: ' + serviceAccountPath);
      }
    } catch (error) {
      this.logger.warn('Firebase Admin SDK init failed, FCM push disabled: ' + error.message);
    }
  }

  async sendPushNotification(
    token: string,
    title: string,
    body: string,
    data?: Record<string, string>,
  ) {
    if (!this.app) return;

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
    } catch (error) {
      this.logger.warn(`FCM push failed: ${error.message}`);
    }
  }

  async sendPushToMultiple(
    tokens: string[],
    title: string,
    body: string,
    data?: Record<string, string>,
  ) {
    if (!this.app || tokens.length === 0) return;

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
    } catch (error) {
      this.logger.warn(`FCM multicast push failed: ${error.message}`);
    }
  }

  getStorageBucket() {
    if (!this.app) return null;
    return this.app.storage().bucket();
  }

  async uploadToStorage(
    filePath: string,
    destination: string,
    contentType?: string,
  ): Promise<string | null> {
    if (!this.app) return null;

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
    } catch (error) {
      this.logger.warn(`Storage upload failed: ${error.message}`);
      return null;
    }
  }

  async deleteFromStorage(destination: string) {
    if (!this.app) return;

    try {
      const bucket = this.app.storage().bucket();
      await bucket.file(destination).delete();
    } catch (error) {
      this.logger.warn(`Storage delete failed: ${error.message}`);
    }
  }

  get firestore() {
    if (!this.app) return null;
    return this.app.firestore();
  }

  async syncUser(user: {
    id: string;
    username: string;
    email: string;
    role: string;
    department?: string | null;
    bio?: string | null;
    avatar?: string | null;
    isActive: boolean;
    lastSeen?: Date | null;
    createdAt: Date;
  }) {
    try {
      const db = this.firestore;
      if (!db) return;
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
    } catch (error) {
      this.logger.warn(`Firestore user sync failed: ${error.message}`);
    }
  }

  async syncMessage(message: {
    id: string;
    content: string;
    type: string;
    image?: string | null;
    senderId: string;
    receiverId?: string | null;
    groupId?: string | null;
    isRead: boolean;
    createdAt: Date;
    sender?: { id: string; username: string; avatar?: string | null };
  }) {
    try {
      const db = this.firestore;
      if (!db) return;
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
    } catch (error) {
      this.logger.warn(`Firestore message sync failed: ${error.message}`);
    }
  }

  async syncGroup(group: {
    id: string;
    name: string;
    avatar?: string | null;
    createdAt: Date;
    members?: string[];
  }) {
    try {
      const db = this.firestore;
      if (!db) return;
      await db.collection('groups').doc(group.id).set({
        id: group.id,
        name: group.name,
        avatar: group.avatar || '',
        members: group.members || [],
        createdAt: group.createdAt.getTime(),
      }, { merge: true });
    } catch (error) {
      this.logger.warn(`Firestore group sync failed: ${error.message}`);
    }
  }
}