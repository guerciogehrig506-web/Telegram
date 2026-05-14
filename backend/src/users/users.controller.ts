import { Body, Controller, Get, Param, Patch, Query, Req } from '@nestjs/common';
import { UsersService } from './users.service';
import { UpdateUserDto } from './dto/update-user.dto';
import { Request } from 'express';

@Controller('users')
export class UsersController {
  constructor(private usersService: UsersService) {}

  @Get('me')
  getMe(@Req() req: Request) {
    return this.usersService.getMe(req['user'].sub);
  }

  @Patch('me')
  updateMe(@Req() req: Request, @Body() dto: UpdateUserDto) {
    return this.usersService.updateMe(req['user'].sub, dto);
  }

  @Patch('me/fcm-token')
  updateFcmToken(@Req() req: Request, @Body() body: { token: string }) {
    return this.usersService.updateFcmToken(req['user'].sub, body.token);
  }

  @Get()
  findAll(
    @Req() req: Request,
    @Query('search') search?: string,
    @Query('department') department?: string,
  ) {
    return this.usersService.findAll(req['user'].sub, search, department);
  }

  @Get(':id')
  findById(@Param('id') id: string) {
    return this.usersService.findById(id);
  }
}