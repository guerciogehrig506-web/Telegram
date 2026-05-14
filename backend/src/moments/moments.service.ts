import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';

@Injectable()
export class MomentsService {
  constructor(private prisma: PrismaService) {}

  async findAll() {
    return this.prisma.moment.findMany({
      include: {
        user: {
          select: { id: true, username: true, avatar: true, department: true },
        },
        likes: {
          select: { userId: true },
        },
      },
      orderBy: { createdAt: 'desc' },
    });
  }

  async create(userId: string, content: string) {
    return this.prisma.moment.create({
      data: { userId, content },
      include: {
        user: {
          select: { id: true, username: true, avatar: true, department: true },
        },
        likes: true,
      },
    });
  }

  async toggleLike(momentId: string, userId: string) {
    const existing = await this.prisma.momentLike.findUnique({
      where: { momentId_userId: { momentId, userId } },
    });

    if (existing) {
      await this.prisma.momentLike.delete({
        where: { id: existing.id },
      });
      return { liked: false };
    } else {
      await this.prisma.momentLike.create({
        data: { momentId, userId },
      });
      return { liked: true };
    }
  }
}