import { IsNotEmpty, IsOptional, IsString, MaxLength } from 'class-validator';

export class CreateGroupDto {
  @IsNotEmpty()
  @IsString()
  @MaxLength(50)
  name: string;

  @IsOptional()
  @IsString()
  avatar?: string;

  @IsNotEmpty()
  memberIds: string[];
}

export class UpdateGroupDto {
  @IsOptional()
  @IsString()
  @MaxLength(50)
  name?: string;

  @IsOptional()
  @IsString()
  avatar?: string;
}

export class AddMemberDto {
  @IsNotEmpty()
  userIds: string[];
}