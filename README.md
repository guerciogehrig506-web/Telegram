# Trio - 团队即时通讯应用

基于 NestJS + Android Jetpack Compose 的团队内部即时通讯应用。

## 项目简介

Trio 是一款专为团队协作设计的即时通讯应用，支持 Android 端，提供消息实时通信、群聊管理、通讯录等功能。

- **项目名称**: Trio
- **目标用户**: 10人+ 团队（支持扩展）
- **部署方式**: 云服务器 + 域名
- **是否商用**: 否，仅团队内部使用
- **当前阶段**: Phase 3 进行中

## 当前架构

```
┌─────────────────────────────────────────────────────────┐
│  Android 客户端 (Jetpack Compose + Kotlin)              │
│  ├─ 认证: REST API (JWT) + TokenManager 持久化          │
│  ├─ 数据: Retrofit + OkHttp (HttpLoggingInterceptor)    │
│  ├─ 实时: WebSocket (typing/online/readReceipt)          │
│  └─ 图片: Firebase Storage (仅上传)                     │
├─────────────────────────────────────────────────────────┤
│  NestJS 后端 (port 3000)                                │
│  ├─ 数据库: SQLite (Prisma ORM)                         │
│  ├─ 文件: Multer 本地存储                               │
│  ├─ 实时: Socket.IO Gateway                             │
│  ├─ 同步: FirebaseService → Firestore (只写)            │
│  └─ 推送: FCM (Firebase Admin SDK)                      │
├─────────────────────────────────────────────────────────┤
│  Admin 管理面板 (admin/index.html)                      │
│  ├─ 登录: Firebase Auth 直接鉴权                        │
│  └─ 数据: Firestore 实时读取                            │
└─────────────────────────────────────────────────────────┘
```

### 数据流方向
- **APP ↔ 后端**: REST API + WebSocket，所有读写走后端
- **后端 → SQLite**: 主数据库，所有数据持久化
- **后端 → Firestore**: 单向同步（用户/消息/群组），供 Admin 看板实时查看
- **Admin ↔ Firestore**: Admin 直接读取 Firestore 数据

## 功能特性

### 已完成
- [x] 用户注册/登录（JWT）
- [x] 1对1聊天（文字 + 图片）
- [x] 群聊（创建/管理/收发消息）
- [x] 消息已读回执
- [x] 打字指示器
- [x] 在线状态
- [x] 通讯录（按部门分组 + 搜索）
- [x] 个人资料编辑（头像/昵称/部门）
- [x] 图片消息气泡 + 全屏预览
- [x] 图片缓存（Coil 2.7）
- [x] 消息搜索（本地关键词高亮 + 导航）
- [x] 骨架屏加载
- [x] 拍照发送（Camera + FileProvider）
- [x] Admin 管理面板（用户/消息/动态管理）
- [x] 页面过渡动画
- [x] 消息气泡入场动画
- [x] 列表项交互动画
- [x] 底部导航栏入场动效

### 待完成
- [ ] 推送通知（FCM，代码框架已就绪）
- [ ] 语音消息
- [ ] 文件发送
- [ ] 暗色模式完善
- [ ] 平板/大屏适配
- [ ] 性能优化（LazyColumn/内存/启动）

## 技术栈

### 后端
- **框架**: NestJS (Node.js)
- **数据库**: SQLite (Prisma ORM)
- **实时通信**: Socket.IO
- **认证**: JWT
- **文件上传**: Multer（图片，20MB 限制）
- **同步**: Firebase Admin SDK（Firestore 单向同步）
- **推送**: Firebase Cloud Messaging (FCM)

### Android
- **语言**: Kotlin
- **UI**: Jetpack Compose
- **网络**: Retrofit + OkHttp + HttpLoggingInterceptor
- **图片加载**: Coil 2.7
- **本地存储**: EncryptedSharedPreferences (JWT Token)

### Admin 看板
- 纯前端 HTML + Firebase Auth + Firestore SDK

### 部署
- **容器化**: Docker + docker-compose
- **数据库**: SQLite（开发）/ PostgreSQL（生产推荐）

## 项目结构

```
Telegram/
├── backend/                  # 后端代码 (NestJS)
│   ├── src/
│   │   ├── admin/            # 管理面板 API (AuthGuard + AdminGuard)
│   │   ├── auth/             # JWT 认证 (register/login/me)
│   │   ├── message/          # 消息 CRUD + WebSocket Gateway
│   │   ├── users/            # 用户管理 (通讯录/资料编辑)
│   │   ├── groups/           # 群组 CRUD
│   │   ├── moments/          # 朋友圈
│   │   ├── upload/           # 文件上传 (Multer)
│   │   ├── firebase/         # FirebaseService (FCM + Firestore 同步)
│   │   ├── prisma/           # Prisma Service
│   │   └── common/           # Guards / Decorators
│   ├── prisma/               # Prisma Schema + Migrations
│   └── uploads/              # 上传文件存储目录
├── android/                  # Android 客户端
│   └── app/src/main/java/com/trio/app/
│       ├── MainActivity.kt   # 应用入口 + TrioApp() 根 Composable
│       ├── data/
│       │   ├── api/          # ApiService (Retrofit) + ApiClient + AuthInterceptor
│       │   ├── model/        # 数据模型 (User/Message/Group/ChatPreview)
│       │   ├── repository/   # AuthRepository (REST API 认证)
│       │   ├── firebase/     # FirebaseAuthManager (FCM/Storage 使用的 Firebase 初始化)
│       │   ├── local/        # TokenManager (JWT 持久化)
│       │   └── SessionManager.kt  # 会话管理 (checkSession/logout/currentUser)
│       ├── viewmodel/        # ViewModel 层 (Auth/Chat/ChatList/Contacts/GroupChat 等)
│       └── ui/
│           ├── screen/       # 页面 (Login/Register/Chat/ChatList/Contacts/Main/Moments 等)
│           ├── components/   # 通用组件 (ChatListItem/ContactListItem/Skeleton 等)
│           ├── theme/        # 主题定义
│           ├── animation/    # 动画工具 (TrioAnimation)
│           └── util/         # 工具类 (WindowAdaptive)
├── admin/                    # Admin 管理看板 (纯前端)
│   └── index.html
└── README.md
```

## 快速开始

### 环境要求
- Node.js 18+
- Android Studio (for Android)
- Java 17

### 后端启动
```bash
cd backend
npm install
npx prisma generate
npx prisma db push
npm run start:dev
# 服务运行在 http://localhost:3000
```

### Android 客户端
1. 用 Android Studio 打开 `android/` 目录
2. 确认 `ApiClient.kt` 中 `BASE_URL` 指向后端地址（模拟器用 `http://10.0.2.2:3000/`）
3. Sync Gradle → Run

### Admin 管理看板
1. 在 [Firebase Console](https://console.firebase.google.com/project/trio-3f8e2/authentication/users) 中创建管理员用户
2. 打开 `admin/index.html`，使用 Firebase Auth 账号登录
3. 看板直接读取 Firestore 数据

## API 端点摘要

| 方法 | 端点 | 说明 |
|------|------|------|
| POST | /auth/register | 用户注册 |
| POST | /auth/login | 用户登录 |
| GET | /users/me | 当前用户信息 |
| GET | /users | 通讯录列表（支持 search/department） |
| PATCH | /users/me | 修改个人资料 |
| POST | /messages | 发送消息 |
| GET | /messages/user | 历史消息 |
| GET | /messages/chats | 最近会话列表 |
| POST | /upload | 上传图片 |
| POST | /groups | 创建群组 |
| GET | /groups/:id | 群组详情 |
| GET | /groups/:id/messages | 群消息历史 |
| GET/POST | /moments | 朋友圈列表/发布 |
| POST | /moments/:id/like | 点赞 toggle |

### WebSocket 事件
- `authenticate` → 客户端鉴权
- `sendMessage` → 发送消息
- `newMessage` → 接收新消息
- `typing` / `stopTyping` → 输入状态
- `userOnline` / `userOffline` → 在线状态
- `markAsRead` → 已读回执

## 数据安全

- **认证**: JWT Token（12h 有效期）
- **存储**: EncryptedSharedPreferences（Token/用户信息）
- **传输**: HTTPS（生产环境）

## 开发规范

### 提交信息格式
```
<type>: <subject>

type: feat, fix, docs, style, refactor, test, chore
```

## 开发进度

- [x] Phase 1A: 后端补齐
- [x] Phase 1B: 前端回接
- [x] Phase 2A: 单人聊天完整体验
- [x] Phase 2B: 社交扩展（群聊等）
- [ ] Phase 3: 动效打磨与适配（进行中）
- [ ] Phase 4: 性能与内存优化
- [ ] Phase 5: 工程化与发布

## 联系方式

项目维护团队