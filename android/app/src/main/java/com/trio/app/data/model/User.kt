package com.trio.app.data.model

import androidx.compose.ui.graphics.Color

data class User(
    val id: String,
    val username: String,
    val email: String,
    val role: String,
    val department: String = "",
    val bio: String = "",
    val avatar: String = "",
    val isActive: Boolean,
    val lastSeen: String = "",
    val createdAt: String,
    val updatedAt: String
)

data class RegisterResponse(val user: User, val token: String)
data class LoginResponse(val user: User, val token: String)

data class Message(
    val id: String,
    val content: String,
    val image: String? = null,
    val type: String? = "text",
    val senderId: String,
    val receiverId: String?,
    val groupId: String?,
    val isRead: Boolean,
    val createdAt: String,
    val sender: User? = null,
    val receiver: User? = null
)

data class ChatResponse(
    val user: User,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int
)

data class MomentUser(
    val id: String,
    val username: String,
    val avatar: String = "",
    val department: String = ""
)

data class MomentLike(
    val userId: String
)

data class MomentResponse(
    val id: String,
    val content: String,
    val userId: String = "",
    val createdAt: String,
    val user: MomentUser,
    val likes: List<MomentLike> = emptyList()
)

enum class OnlineStatus { ONLINE, AWAY, OFFLINE }

data class Contact(
    val user: User,
    val onlineStatus: OnlineStatus = OnlineStatus.OFFLINE,
    val avatarGradient: List<Color> = listOf(Color(0xFF4FACFE), Color(0xFF00F2FE))
)

data class ChatPreview(
    val contact: Contact,
    val lastMessage: String,
    val lastMessageTime: String,
    val unreadCount: Int,
    val readStatus: ReadStatus
)

enum class ReadStatus { SENDING, SENT, DELIVERED, READ }