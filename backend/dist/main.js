"use strict";
Object.defineProperty(exports, "__esModule", { value: true });
const core_1 = require("@nestjs/core");
const app_module_1 = require("./app.module");
const common_1 = require("@nestjs/common");
const session = require("express-session");
const createFileStore = require('session-file-store');
const path_1 = require("path");
const FileStore = createFileStore(session);
async function bootstrap() {
    const app = await core_1.NestFactory.create(app_module_1.AppModule);
    app.useStaticAssets((0, path_1.join)(__dirname, '..', 'public'));
    app.useStaticAssets((0, path_1.join)(__dirname, '..', 'uploads'), { prefix: '/uploads' });
    app.setBaseViewsDir((0, path_1.join)(__dirname, '..', 'views'));
    app.setViewEngine('ejs');
    app.use(session({
        store: new FileStore({
            path: (0, path_1.join)(__dirname, '..', 'sessions'),
            ttl: 7 * 24 * 60 * 60,
            retries: 0,
        }),
        secret: process.env.SESSION_SECRET || 'trio-admin-session-secret',
        resave: false,
        saveUninitialized: false,
        cookie: { maxAge: 7 * 24 * 60 * 60 * 1000, httpOnly: true },
    }));
    app.useGlobalPipes(new common_1.ValidationPipe());
    app.enableCors({
        origin: '*',
        methods: 'GET,HEAD,PUT,PATCH,POST,DELETE',
        credentials: true,
    });
    const port = process.env.PORT || 3000;
    await app.listen(port, '0.0.0.0');
    console.log(`Trio Backend running on http://0.0.0.0:${port}`);
}
bootstrap();
//# sourceMappingURL=main.js.map