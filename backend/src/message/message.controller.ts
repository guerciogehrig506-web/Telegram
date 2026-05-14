import { Controller, Post, Body, Get, Query, Req } from '@nestjs/common';
import { MessageService } from './message.service';
import { CreateMessageDto } from './dto';
import { Request } from 'express';

@Controller('messages')
export class MessageController {
  constructor(private messageService: MessageService) {}

  @Post()
  create(@Body() createMessageDto: CreateMessageDto, @Req() req: Request) {
    return this.messageService.createMessage(createMessageDto, req['user'].sub);
  }

  @Get('chats')
  getChats(@Req() req: Request) {
    return this.messageService.getChats(req['user'].sub);
  }

  @Get('user')
  getMessagesBetweenUsers(
    @Req() req: Request,
    @Query('otherUserId') otherUserId: string,
  ) {
    return this.messageService.getMessagesBetweenUsers(req['user'].sub, otherUserId);
  }

  @Get('group')
  getGroupMessages(@Query('groupId') groupId: string) {
    return this.messageService.getGroupMessages(groupId);
  }
}
