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
var __param = (this && this.__param) || function (paramIndex, decorator) {
    return function (target, key) { decorator(target, key, paramIndex); }
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AdminController = void 0;
const common_1 = require("@nestjs/common");
const public_decorator_1 = require("../common/decorators/public.decorator");
const admin_auth_guard_1 = require("./admin-auth.guard");
const prisma_service_1 = require("../prisma/prisma.service");
const bcrypt = require("bcrypt");
let AdminController = class AdminController {
    constructor(prisma) {
        this.prisma = prisma;
    }
    getLogin(req) {
        if (req.session?.admin)
            return { admin: req.session.admin };
        return { error: null };
    }
    async postLogin(email, password, req, res) {
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
    async logout(req, res) {
        req.session.destroy(() => { });
        return res.redirect('/admin/login');
    }
    async dashboard(req) {
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
    async users(req) {
        const users = await this.prisma.user.findMany({
            orderBy: { createdAt: 'desc' },
        });
        return { admin: req.session.admin, users };
    }
    async messages(req) {
        const msgs = await this.prisma.message.findMany({
            include: { sender: true, receiver: true },
            orderBy: { createdAt: 'desc' },
            take: 100,
        });
        return { admin: req.session.admin, messages: msgs };
    }
    async moments(req) {
        const moments = await this.prisma.moment.findMany({
            include: { user: true, _count: { select: { likes: true } } },
            orderBy: { createdAt: 'desc' },
        });
        return { admin: req.session.admin, moments };
    }
};
exports.AdminController = AdminController;
__decorate([
    (0, common_1.Get)('login'),
    (0, common_1.Render)('admin/login'),
    __param(0, (0, common_1.Req)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], AdminController.prototype, "getLogin", null);
__decorate([
    (0, common_1.Post)('login'),
    __param(0, (0, common_1.Body)('email')),
    __param(1, (0, common_1.Body)('password')),
    __param(2, (0, common_1.Req)()),
    __param(3, (0, common_1.Res)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [String, String, Object, Object]),
    __metadata("design:returntype", Promise)
], AdminController.prototype, "postLogin", null);
__decorate([
    (0, common_1.Get)('logout'),
    __param(0, (0, common_1.Req)()),
    __param(1, (0, common_1.Res)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], AdminController.prototype, "logout", null);
__decorate([
    (0, common_1.Get)(),
    (0, common_1.UseGuards)(admin_auth_guard_1.AdminAuthGuard),
    (0, common_1.Render)('admin/dashboard'),
    __param(0, (0, common_1.Req)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], AdminController.prototype, "dashboard", null);
__decorate([
    (0, common_1.Get)('users'),
    (0, common_1.UseGuards)(admin_auth_guard_1.AdminAuthGuard),
    (0, common_1.Render)('admin/users'),
    __param(0, (0, common_1.Req)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], AdminController.prototype, "users", null);
__decorate([
    (0, common_1.Get)('messages'),
    (0, common_1.UseGuards)(admin_auth_guard_1.AdminAuthGuard),
    (0, common_1.Render)('admin/messages'),
    __param(0, (0, common_1.Req)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], AdminController.prototype, "messages", null);
__decorate([
    (0, common_1.Get)('moments'),
    (0, common_1.UseGuards)(admin_auth_guard_1.AdminAuthGuard),
    (0, common_1.Render)('admin/moments'),
    __param(0, (0, common_1.Req)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", Promise)
], AdminController.prototype, "moments", null);
exports.AdminController = AdminController = __decorate([
    (0, common_1.Controller)('admin'),
    (0, public_decorator_1.Public)(),
    __metadata("design:paramtypes", [prisma_service_1.PrismaService])
], AdminController);
//# sourceMappingURL=admin.controller.js.map