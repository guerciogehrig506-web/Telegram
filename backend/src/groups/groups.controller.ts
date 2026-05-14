import { Body, Controller, Delete, Get, Param, Patch, Post, Req } from '@nestjs/common';
import { GroupsService } from './groups.service';
import { CreateGroupDto, UpdateGroupDto, AddMemberDto } from './dto/group.dto';
import { Request } from 'express';

@Controller('groups')
export class GroupsController {
  constructor(private groupsService: GroupsService) {}

  @Post()
  create(@Req() req: Request, @Body() dto: CreateGroupDto) {
    return this.groupsService.create(dto, req['user'].sub);
  }

  @Get()
  findAll(@Req() req: Request) {
    return this.groupsService.findAll(req['user'].sub);
  }

  @Get(':id')
  findById(@Param('id') id: string) {
    return this.groupsService.findById(id);
  }

  @Patch(':id')
  update(@Param('id') id: string, @Body() dto: UpdateGroupDto) {
    return this.groupsService.update(id, dto);
  }

  @Post(':id/members')
  addMembers(@Param('id') id: string, @Body() dto: AddMemberDto) {
    return this.groupsService.addMembers(id, dto.userIds);
  }

  @Delete(':id/members/:userId')
  removeMember(@Param('id') id: string, @Param('userId') userId: string) {
    return this.groupsService.removeMember(id, userId);
  }

  @Delete(':id')
  delete(@Param('id') id: string) {
    return this.groupsService.delete(id);
  }
}