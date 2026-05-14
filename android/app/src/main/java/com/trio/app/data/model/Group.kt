package com.trio.app.data.model

data class Group(
    val id: String,
    val name: String,
    val avatar: String = "",
    val createdAt: String,
    val members: List<GroupMember> = emptyList()
)

data class GroupMember(
    val id: String,
    val user: GroupUser
)

data class GroupUser(
    val id: String,
    val username: String,
    val avatar: String = "",
    val bio: String = "",
    val lastSeen: String = ""
)