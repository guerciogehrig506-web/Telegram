import { Injectable, ConflictException, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { PrismaService } from '../prisma/prisma.service';
import { FirebaseService } from '../firebase/firebase.service';
import { RegisterDto, LoginDto } from './dto';

@Injectable()
export class AuthService {
  constructor(
    private prisma: PrismaService,
    private jwtService: JwtService,
    private firebase: FirebaseService,
  ) {}

  async register(registerDto: RegisterDto) {
    const normalizedEmail = registerDto.email.toLowerCase().trim();

    const existing = await this.prisma.user.findFirst({
      where: { email: normalizedEmail },
    });
    if (existing) {
      throw new ConflictException('该邮箱已被注册');
    }

    const hashedPassword = await bcrypt.hash(registerDto.password, 10);

    const user = await this.prisma.user.create({
      data: {
        username: registerDto.username,
        email: normalizedEmail,
        password: hashedPassword,
      },
    });

    const payload = { sub: user.id, username: user.username, role: user.role };
    const token = this.jwtService.sign(payload);

    this.firebase.syncUser(user).catch(() => {});

    return { user: this.excludePassword(user), token };
  }

  async login(loginDto: LoginDto) {
    const normalizedEmail = loginDto.email.toLowerCase().trim();

    const user = await this.prisma.user.findFirst({
      where: { email: normalizedEmail },
    });

    if (!user || !user.isActive) {
      throw new UnauthorizedException('邮箱或密码错误');
    }

    const isPasswordValid = await bcrypt.compare(loginDto.password, user.password);
    if (!isPasswordValid) {
      throw new UnauthorizedException('邮箱或密码错误');
    }

    const payload = { sub: user.id, username: user.username, role: user.role };
    const token = this.jwtService.sign(payload);

    this.firebase.syncUser(user).catch(() => {});

    return { user: this.excludePassword(user), token };
  }

  private excludePassword(user: any) {
    const { password, ...result } = user;
    return result;
  }
}
