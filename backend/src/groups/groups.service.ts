import { Injectable } from '@nestjs/common';
import { PrismaService } from '../prisma/prisma.service';
import { FirebaseService } from '../firebase/firebase.service';
import { CreateGroupDto, UpdateGroupDto } from './dto/group.dto';

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

@Injectable()
export class GroupsService {
  constructor(
    private prisma: PrismaService,
    private firebase: FirebaseService,
  ) {}

  async create(dto: CreateGroupDto, creatorId: string) {
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
    }).catch(() => {});

    return group;
  }

  async findAll(userId: string) {
    return this.prisma.group.findMany({
      where: {
        members: { some: { userId } },
      },
      select: GROUP_SELECT,
      orderBy: { createdAt: 'desc' },
    });
  }

  async findById(id: string) {
    return this.prisma.group.findUnique({
      where: { id },
      select: GROUP_SELECT,
    });
  }

  async update(id: string, dto: UpdateGroupDto) {
    return this.prisma.group.update({
      where: { id },
      data: {
        ...(dto.name !== undefined && { name: dto.name }),
        ...(dto.avatar !== undefined && { avatar: dto.avatar }),
      },
      select: GROUP_SELECT,
    });
  }

  async addMembers(id: string, userIds: string[]) {
    const group = await this.prisma.group.findUnique({ where: { id }, select: { id: true } });
    if (!group) return null;

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

  async removeMember(id: string, userId: string) {
    await this.prisma.groupMember.deleteMany({
      where: { groupId: id, userId },
    });
    return this.findById(id);
  }

  async delete(id: string) {
    await this.prisma.group.delete({ where: { id } });
    return { success: true };
  }
}