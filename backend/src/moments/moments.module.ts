import { Module } from '@nestjs/common';
import { MomentsService } from './moments.service';
import { MomentsController } from './moments.controller';
import { PrismaModule } from '../prisma/prisma.module';

@Module({
  imports: [PrismaModule],
  providers: [MomentsService],
  controllers: [MomentsController],
})
export class MomentsModule {}