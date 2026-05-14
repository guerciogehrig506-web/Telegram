import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { UpdateUserDto } from './dto/update-user.dto';

@Injectable()
export class UsersService {
  constructor(private prisma: PrismaService) {}

  async findAll(excludeUserId?: string, search?: string, department?: string) {
    const where: any = {};
    if (excludeUserId) where.id = { not: excludeUserId };
    if (search) {
      where.OR = [
        { username: { contains: search } },
        { email: { contains: search } },
      ];
    }
    if (department) where.department = department;

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

  async findById(id: string) {
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

  async getMe(userId: string) {
    return this.findById(userId);
  }

  async updateMe(userId: string, dto: UpdateUserDto) {
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

  async updateFcmToken(userId: string, token: string) {
    return this.prisma.user.update({
      where: { id: userId },
      data: { fcmToken: token },
    });
  }
}