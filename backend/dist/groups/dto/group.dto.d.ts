export declare class CreateGroupDto {
    name: string;
    avatar?: string;
    memberIds: string[];
}
export declare class UpdateGroupDto {
    name?: string;
    avatar?: string;
}
export declare class AddMemberDto {
    userIds: string[];
}
