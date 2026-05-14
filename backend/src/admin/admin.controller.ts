import {
  Controller,
  Get,
  Post,
  Render,
  Req,
  Res,
  UseGuards,
  Body,
} from '@nestjs/common';
import { Request, Response } from 'express';
import { Public } from '../common/decorators/public.decorator';
import { AdminAuthGuard } from './admin-auth.guard';
import { PrismaService } from '../prisma/prisma.service';
import * as bcrypt from 'bcrypt';

@Controller('admin')
@Public()
export class AdminController {
  constructor(private prisma: PrismaService) {}

  @Get('login')
  @Render('admin/login')
  getLogin(@Req() req: Request) {
    if (req.session?.admin) return { admin: req.session.admin };
    return { error: null };
  }

  @Post('login')
  async postLogin(
    @Body('email') email: string,
    @Body('password') password: string,
    @Req() req: Request,
    @Res() res: Response,
  ) {
    const user = await this.prisma.user.findUnique({ where: { email } });
    if (!user || user.role !== 'ADMIN') {
      return res.render('admin/login', { error: '账号不存在或非管理员' });
    }
    const valid = await bcrypt.compare(password, user.password);
    if (!valid) {
      return res.render('admin/login', { error: '密码错误' });
    }
    req.session.admin = { id: user.id, username: user.username, email: user.email };
    return res.redirect('/admin');
  }

  @Get('logout')
  async logout(@Req() req: Request, @Res() res: Response) {
    req.session.destroy(() => {});
    return res.redirect('/admin/login');
  }

  @Get()
  @UseGuards(AdminAuthGuard)
  @Render('admin/dashboard')
  async dashboard(@Req() req: Request) {
    const [users, messages, moments, groups] = await Promise.all([
      this.prisma.user.count(),
      this.prisma.message.count(),
      this.prisma.moment.count(),
      this.prisma.group.count(),
    ]);
    return {
      admin: req.session.admin,
      stats: { users, messages, moments, groups },
    };
  }

  @Get('users')
  @UseGuards(AdminAuthGuard)
  @Render('admin/users')
  async users(@Req() req: Request) {
    const users = await this.prisma.user.findMany({
      orderBy: { createdAt: 'desc' },
    });
    return { admin: req.session.admin, users };
  }

  @Get('messages')
  @UseGuards(AdminAuthGuard)
  @Render('admin/messages')
  async messages(@Req() req: Request) {
    const msgs = await this.prisma.message.findMany({
      include: { sender: true, receiver: true },
      orderBy: { createdAt: 'desc' },
      take: 100,
    });
    return { admin: req.session.admin, messages: msgs };
  }

  @Get('moments')
  @UseGuards(AdminAuthGuard)
  @Render('admin/moments')
  async moments(@Req() req: Request) {
    const moments = await this.prisma.moment.findMany({
      include: { user: true, _count: { select: { likes: true } } },
      orderBy: { createdAt: 'desc' },
    });
    return { admin: req.session.admin, moments };
  }
}