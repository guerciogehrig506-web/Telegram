package com.trio.app.data.firebase

import com.google.firebase.firestore.DocumentSnapshot

data class FirestoreUser(
    val id: String,
    val username: String,
    val email: String = "",
    val role: String = "member",
    val bio: String = "",
    val avatar: String = "",
    val isActive: Boolean = false
) {
    companion object {
        fun fromDoc(doc: DocumentSnapshot): FirestoreUser {
            return FirestoreUser(
                id = doc.id,
                username = doc.getString("username") ?: "",
                email = doc.getString("email") ?: "",
                role = doc.getString("role") ?: "member",
                bio = doc.getString("bio") ?: "",
                avatar = doc.getString("avatar") ?: "",
                isActive = doc.getBoolean("isActive") ?: false
            )
        }
    }
}

data class FirestoreMoment(
    val id: String,
    val username: String,
    val content: String,
    val createdAt: Long,
    val likedByMe: Boolean = false,
    val likeCount: Int = 0
) {
    companion object {
        fun fromDoc(doc: DocumentSnapshot, currentUserId: String?): FirestoreMoment {
            val likes = doc.get("likes") as? List<String> ?: emptyList()
            return FirestoreMoment(
                id = doc.id,
                username = doc.getString("username") ?: "",
                content = doc.getString("content") ?: "",
                createdAt = doc.getLong("createdAt") ?: 0L,
                likedByMe = currentUserId != null && likes.contains(currentUserId),
                likeCount = likes.size
            )
        }
    }
}