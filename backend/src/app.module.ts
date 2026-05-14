import { Module } from '@nestjs/common';
import { APP_GUARD } from '@nestjs/core';
import { ConfigModule } from '@nestjs/config';
import { AuthModule } from './auth/auth.module';
import { MessageModule } from './message/message.module';
import { AdminModule } from './admin/admin.module';
import { UsersModule } from './users/users.module';
import { MomentsModule } from './moments/moments.module';
import { UploadModule } from './upload/upload.module';
import { GroupsModule } from './groups/groups.module';
import { FirebaseModule } from './firebase/firebase.module';
import { PrismaModule } from './prisma/prisma.module';
import { JwtAuthGuard } from './common/guards/jwt-auth.guard';

@Module({
  imports: [
    ConfigModule.forRoot({ isGlobal: true }),
    AuthModule,
    MessageModule,
    AdminModule,
    UsersModule,
    MomentsModule,
    UploadModule,
    GroupsModule,
    FirebaseModule,
    PrismaModule,
  ],
  providers: [
    { provide: APP_GUARD, useClass: JwtAuthGuard },
  ],
})
export class AppModule {}