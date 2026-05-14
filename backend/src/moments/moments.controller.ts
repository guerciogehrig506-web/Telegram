import { Controller, Get, Post, Param, Body, Req } from '@nestjs/common';
import { MomentsService } from './moments.service';
import { Request } from 'express';

@Controller('moments')
export class MomentsController {
  constructor(private momentsService: MomentsService) {}

  @Get()
  findAll() {
    return this.momentsService.findAll();
  }

  @Post()
  create(@Req() req: Request, @Body() body: { content: string }) {
    return this.momentsService.create(req['user'].sub, body.content);
  }

  @Post(':id/like')
  toggleLike(@Param('id') id: string, @Req() req: Request) {
    return this.momentsService.toggleLike(id, req['user'].sub);
  }
}