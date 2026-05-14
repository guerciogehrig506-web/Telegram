# Trio 项目交接提示词 — 给下一个 AI 开发者

---

你正在接手开发 **Trio**，一个基于 NestJS 后端 + Android Jetpack Compose 的团队内部即时通讯应用（类似简版 Telegram/微信）。

## 1. 项目概况

| 项目 | 详情 |
|------|------|
| **名称** | Trio |
| **用途** | 10人+ 团队内部通讯，非商用 |
| **平台** | Android (Kotlin + Jetpack Compose) |
| **后端** | NestJS (Node.js) + Prisma + SQLite |
| **管理看板** | 纯前端 HTML (admin/index.html)，用 Firebase Auth 登录读取 Firestore |
| **当前阶段** | Phase 3 动效打磨与适配（Phase 1-2 已完成） |

## 2. 核心架构（必须理解）

```
Android APP ──REST API (JWT)──▶ NestJS 后端 ──▶ SQLite (主数据库)
    │                              │
    │                              ├──▶ Firestore (单向同步，仅写)
    │                              │
    │                              └──▶ FCM 推送
    │
    └── Firebase Storage (图片上传，APP 直接上传)

Admin 看板 ──Firebase Auth──▶ Firebase ──▶ Firestore (只读)
```

### 关键理解点
- **APP 完全走后端 REST API**，不直连 Firebase Auth/Firestore
- **后端 SQLite 是唯一数据源**，Firestore 只是从后端单向同步过去的**只读副本**（给 Admin 看板用）
- **APP 图片上传走 Firebase Storage**（直接上传，不走后端中转）
- **WebSocket (Socket.IO)** 用于实时通信（在线状态/输入中/新消息推送）

## 3. 技术栈

### 后端 (`backend/`)
- NestJS + Prisma ORM + SQLite (`backend/prisma/dev.db`)
- JWT 认证（12h 有效期）
- Socket.IO Gateway（实时推送）
- Multer 本地文件存储（`backend/uploads/`）
- Firebase Admin SDK（FCM 推送 + Firestore 同步）
- Port: **3000**

### Android (`android/`)
- Kotlin + Jetpack Compose
- Retrofit + OkHttp（REST API 调用）
- Coil 2.7（图片加载）
- EncryptedSharedPreferences（JWT Token 持久化）
- Firebase SDK（仅用于 Storage 上传 + FCM）

### Admin (`admin/`)
- 单文件 HTML，Firebase Auth 登录 + Firestore SDK 直接读取

## 4. 项目结构（重要文件标注）

```
Telegram/
├── README.md                           # 完整项目文档
├── 下一步开发.md                        # 开发计划和进度
│
├── backend/
│   ├── .env                            # JWT_SECRET / DATABASE_URL 等
│   ├── package.json
│   ├── prisma/schema.prisma            # 数据模型定义
│   ├── prisma/dev.db                   # SQLite 数据库文件
│   ├── src/
│   │   ├── main.ts                     # 入口 (port 3000, CORS, 全局管道)
│   │   ├── app.module.ts               # 根模块
│   │   ├── auth/auth.service.ts        # 注册/登录/同步用户到 Firestore
│   │   ├── message/message.service.ts  # 消息 CRUD + 同步到 Firestore + FCM
│   │   ├── message/message.gateway.ts  # WebSocket 网关
│   │   ├── groups/groups.service.ts    # 群组 CRUD + 同步
│   │   ├── firebase/firebase.service.ts # Firebase Admin (FCM + Firestore)
│   │   └── common/guards/              # JWT AuthGuard, Public 装饰器
│   └── uploads/                        # 上传文件目录
│
├── android/
│   └── app/src/main/java/com/trio/app/
│       ├── MainActivity.kt             # ★ 入口：onCreate 初始化 + TrioApp() 根组件
│       ├── data/
│       │   ├── api/ApiService.kt       # ★ Retrofit 接口定义（所有 REST 端点）
│       │   ├── api/ApiClient.kt        # ★ OkHttp + Retrofit 配置 (BASE_URL!)
│       │   ├── api/AuthInterceptor.kt  # JWT Token 自动附加
│       │   ├── model/                  # 数据模型: User, Message, Group, ChatPreview
│       │   ├── repository/AuthRepository.kt  # 认证 REST API 封装
│       │   ├── firebase/FirebaseAuthManager.kt  # Firebase 初始化 + logout
│       │   ├── local/TokenManager.kt   # JWT Token 持久化
│       │   └── SessionManager.kt       # ★ 会话管理: checkSession/logout/currentUser
│       ├── viewmodel/                  # ViewModel: Auth/Chat/ChatList/Contacts/GroupChat/等
│       ├── ui/
│       │   ├── screen/                 # Compose 页面
│       │   ├── components/             # 可复用组件
│       │   ├── animation/              # 动画工具 (TrioAnimation/AnimatedListItem)
│       │   └── util/WindowAdaptive.kt  # 屏幕自适应
│       └── TrioFirebaseService.kt      # FCM 推送服务
│
├── admin/
│   └── index.html                      # 管理看板 (Firebase Auth + Firestore)
│
└── .firebaserc                         # Firebase 项目: trio-3f8e2
```

## 5. 如何运行

### 后端
```bash
cd backend
npm install
npx prisma generate
npx prisma db push
npm run start:dev
# → 运行在 http://localhost:3000
```

### Android
1. Android Studio 打开 `android/` 目录
2. `ApiClient.kt` 的 `BASE_URL`：
   - 模拟器: `http://10.0.2.2:3000/`
   - 真机: 改为服务器实际 IP
3. Sync Gradle → Run
4. 构建命令: `$env:JAVA_HOME="C:\Program Files\Java\jdk-17"; .\gradlew assembleDebug`

### Admin 看板
1. 在 [Firebase Console → Authentication](https://console.firebase.google.com/project/trio-3f8e2/authentication/users) 创建用户
2. 浏览器打开 `admin/index.html`，用 Firebase Auth 账号登录

## 6. 开发历史回顾

### Phase 1: 后端框架 + 前端回接 ✅
- NestJS + Prisma + SQLite 搭建
- JWT 全局鉴权 + WebSocket 实时通信
- Android 从 Mock/Firebase 直接访问切到 REST API

### Phase 2A: 单人聊天完整体验 ✅
- 图片收发（相册+拍照）、Coil 缓存
- 消息搜索（关键词高亮+导航）
- 骨架屏、打字指示器、在线状态

### Phase 2B: 社交扩展 ✅
- 群组 CRUD + 群聊消息
- 创建群组页面 + 群聊导航

### Phase 3: 动效打磨与适配（进行中）
- ✅ 导航页面过渡 (slideInHorizontally + fadeIn/Out, 350ms)
- ✅ 消息气泡入场动画 (AnimatedVisibility)
- ✅ 列表项交互动画 (AnimatedListItem)
- ✅ 底部导航栏入场动效
- ✅ 按压缩放反馈 (pressScale Modifier)
- ✅ 窗口自适应 (WindowAdaptive)
- ⏳ 下拉刷新、触觉反馈、侧滑手势、暗色模式、平板适配

### 最近一次代码清理 (2026-05-10)
删除了 7 个完全未被引用的冗余文件：
- `FirestoreModels.kt` — Firestore 数据模型（APP 已不直连 Firestore）
- `ChatRepository.kt`, `UserRepository.kt`, `MomentsRepository.kt` — 空仓库类
- `SocketManager.kt` — Socket.IO 客户端（未使用，WebSocket 走后端 Gateway）
- `TasksPlaceholderScreen.kt` — 占位页面（未在任何导航中使用）
- `NgrokInterceptor.kt` — ngrok 隧道头注入（已停用 ngrok）

精简了 `FirebaseAuthManager.kt`（移除未使用的 register/login/checkSession/FirebaseResult/FirestoreState/FirestoreService）。

## 7. 已知问题和下一步任务

### 🔴 高优先级

**1. FirebaseAuthManager.getUserId() 返回 null 的 bug**  
`FirebaseAuthManager.getUserId()` 返回 `auth.currentUser?.uid`（Firebase Auth UID），但 APP 已切到 REST API 认证，Firebase Auth 未登录，因此返回 null。以下文件需要替换为 `SessionManager.currentUser.value?.id`：
- `ChatScreen.kt:571` — 消息归属判断（isSent）
- `MomentsScreen.kt:65,116` — 当前用户 ID
- `ProfileEditScreen.kt:81` — 用户 ID（有 `?: return` 空安全）
- `SettingsScreen.kt:74` — 用户 ID（有 `?: return` 空安全）
- `CreateGroupViewModel.kt:25,40` — 当前用户 ID

**2. AAPT2 构建问题**  
当前 Windows 环境缺少 Universal C Runtime，可能导致 `processDebugResources` 失败。如需恢复 AAPT2 守护进程，安装 [UCRT](https://support.microsoft.com/en-us/topic/update-for-universal-c-runtime-in-windows-c0514201-7fe6-95a3-b0a5-287930f3560c)，或在 `gradle.properties` 中添加：
```properties
android.aapt2.useDaemon=false
```

### 🟡 中优先级
- **Phase 3 剩余项**: 下拉刷新动画、触觉反馈、侧滑手势、暗色模式、平板适配
- **Phase 4**: 性能优化（LazyColumn/内存/启动/LeakCanary）
- **FCM 推送**: 代码框架已就绪（`TrioFirebaseService.kt`），需配置 `google-services.json`

### 🟢 低优先级
- Phase 5: 单元测试、UI测试、Release 签名、Play Store 上架
- Admin 看板角色校验（目前所有 Firebase Auth 用户均可登录看板）

## 8. 开发注意事项

- **BASE_URL**: Android 模拟器用 `10.0.2.2:3000`，真机改实际 IP
- **数据流**: APP ↔ 后端 REST API ↔ SQLite。不要绕过后端直连 Firestore
- **图片上传**: 走 Firebase Storage（APP 直传），不是后端 Multer（Multer 用于头像等小文件）
- **AuthRepository**: `object` 单例，不是 `class`
- **SessionManager**: `object` 单例，存储 `currentUser: StateFlow<User?>`，所有获取当前用户的操作都应通过它
- **JWT Token**: 12h 有效期，存储在 EncryptedSharedPreferences，由 AuthInterceptor 自动附加
- **WebSocket**: 后端 MessageGateway 处理，事件: authenticate/sendMessage/newMessage/typing/userOnline/markAsRead
- **Prisma Schema**: 修改后需运行 `npx prisma generate && npx prisma db push`

## 9. ChatMessage 类型注意事项

ChatScreen.kt 中定义了 `data class ChatMessage`（在 `com.trio.app.ui.screen` 包），ChatViewModel 通过 `import com.trio.app.ui.screen.ChatMessage` 引用它。**不要**在 ViewModel 包中再定义一个 ChatMessage，会导致类型冲突。

## 10. 构建命令

```bash
# Android (PowerShell)
cd android
$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
.\gradlew assembleDebug

# 后端
cd backend
npm run start:dev
```