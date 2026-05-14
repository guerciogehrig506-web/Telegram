import { IsNotEmpty } from 'class-validator';

export class CreateMessageDto {
  @IsNotEmpty()
  content: string;

  image?: string;
  type?: string;
  receiverId?: string;
  groupId?: string;
  senderId?: string;
}