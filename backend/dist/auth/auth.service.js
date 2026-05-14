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
exports.AuthService = void 0;
const common_1 = require("@nestjs/common");
const jwt_1 = require("@nestjs/jwt");
const bcrypt = require("bcrypt");
const prisma_service_1 = require("../prisma/prisma.service");
const firebase_service_1 = require("../firebase/firebase.service");
let AuthService = class AuthService {
    constructor(prisma, jwtService, firebase) {
        this.prisma = prisma;
        this.jwtService = jwtService;
        this.firebase = firebase;
    }
    async register(registerDto) {
        const normalizedEmail = registerDto.email.toLowerCase().trim();
        const existing = await this.prisma.user.findFirst({
            where: { email: normalizedEmail },
        });
        if (existing) {
            throw new common_1.ConflictException('该邮箱已被注册');
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
        this.firebase.syncUser(user).catch(() => { });
        return { user: this.excludePassword(user), token };
    }
    async login(loginDto) {
        const normalizedEmail = loginDto.email.toLowerCase().trim();
        const user = await this.prisma.user.findFirst({
            where: { email: normalizedEmail },
        });
        if (!user || !user.isActive) {
            throw new common_1.UnauthorizedException('邮箱或密码错误');
        }
        const isPasswordValid = await bcrypt.compare(loginDto.password, user.password);
        if (!isPasswordValid) {
            throw new common_1.UnauthorizedException('邮箱或密码错误');
        }
        const payload = { sub: user.id, username: user.username, role: user.role };
        const token = this.jwtService.sign(payload);
        this.firebase.syncUser(user).catch(() => { });
        return { user: this.excludePassword(user), token };
    }
    excludePassword(user) {
        const { password, ...result } = user;
        return result;
    }
};
exports.AuthService = AuthService;
exports.AuthService = AuthService = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [prisma_service_1.PrismaService,
        jwt_1.JwtService,
        firebase_service_1.FirebaseService])
], AuthService);
//# sourceMappingURL=auth.service.js.map