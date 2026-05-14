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
exports.UploadController = void 0;
const common_1 = require("@nestjs/common");
const platform_express_1 = require("@nestjs/platform-express");
const public_decorator_1 = require("../common/decorators/public.decorator");
const multer_1 = require("multer");
const path_1 = require("path");
const fs_1 = require("fs");
const crypto_1 = require("crypto");
const uploadDir = (0, path_1.join)(__dirname, '..', '..', 'uploads');
if (!(0, fs_1.existsSync)(uploadDir)) {
    (0, fs_1.mkdirSync)(uploadDir, { recursive: true });
}
let UploadController = class UploadController {
    uploadFile(file) {
        return { url: `/uploads/${file.filename}` };
    }
};
exports.UploadController = UploadController;
__decorate([
    (0, common_1.Post)(),
    (0, common_1.UseInterceptors)((0, platform_express_1.FileInterceptor)('file', {
        storage: (0, multer_1.diskStorage)({
            destination: (_req, _file, cb) => cb(null, uploadDir),
            filename: (_req, file, cb) => {
                const unique = `${Date.now()}-${(0, crypto_1.randomBytes)(4).toString('hex')}`;
                cb(null, unique + (0, path_1.extname)(file.originalname));
            },
        }),
        limits: { fileSize: 20 * 1024 * 1024 },
        fileFilter: (_req, file, cb) => {
            const allowed = ['image/jpeg', 'image/png', 'image/gif', 'image/webp'];
            if (allowed.includes(file.mimetype)) {
                cb(null, true);
            }
            else {
                cb(new Error('仅支持 JPEG/PNG/GIF/WebP 图片'), false);
            }
        },
    })),
    __param(0, (0, common_1.UploadedFile)()),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object]),
    __metadata("design:returntype", void 0)
], UploadController.prototype, "uploadFile", null);
exports.UploadController = UploadController = __decorate([
    (0, common_1.Controller)('upload'),
    (0, public_decorator_1.Public)()
], UploadController);
//# sourceMappingURL=upload.controller.js.map