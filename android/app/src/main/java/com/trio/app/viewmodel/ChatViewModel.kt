package com.trio.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.trio.app.data.SessionManager
import com.trio.app.data.api.ApiClient
import com.trio.app.data.api.SendMessageRequest
import com.trio.app.data.model.Message
import com.trio.app.data.model.User
import com.trio.app.ui.screen.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class ChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<ChatMessage>>(emptyList())
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private var currentUserId: String = ""
    private var chatUserId: String = ""

    fun init(userId: String) {
        chatUserId = userId
        currentUserId = SessionManager.currentUser.value?.id ?: ""
        loadMessages()
    }

    fun loadMessages() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getMessages(chatUserId)
                if (response.isSuccessful) {
                    val msgs = response.body() ?: emptyList()
                    _messages.value = msgs.map { m ->
                        ChatMessage(
                            id = m.id,
                            sender = m.sender ?: User(m.senderId, "", "", "USER", isActive = true, createdAt = "", updatedAt = ""),
                            content = m.content,
                            image = m.image,
                            timestamp = m.createdAt.toLongOrNull() ?: System.currentTimeMillis(),
                            isSent = m.senderId == currentUserId,
                            isRead = m.isRead
                        )
                    }
                }
            } catch (_: Exception) {}
            _isLoading.value = false
        }
    }

    fun sendMessage(content: String) {
        val currentUser = SessionManager.currentUser.value ?: return
        viewModelScope.launch {
            try {
                ApiClient.apiService.sendMessage(
                    SendMessageRequest(content = content, type = "text", receiverId = chatUserId)
                )
                loadMessages()
            } catch (_: Exception) {}
        }
    }

    fun sendImage(uri: Uri, contentResolver: android.content.ContentResolver) {
        if (_isUploading.value) return
        _isUploading.value = true
        viewModelScope.launch {
            try {
                val imageUrl = uploadToFirebaseStorage(uri, contentResolver)
                if (imageUrl != null) {
                    ApiClient.apiService.sendMessage(
                        SendMessageRequest(content = "[图片]", type = "image", receiverId = chatUserId)
                    )
                    loadMessages()
                }
            } catch (_: Exception) {}
            _isUploading.value = false
        }
    }

    private suspend fun uploadToFirebaseStorage(uri: Uri, contentResolver: android.content.ContentResolver): String? {
        return withContext(Dispatchers.IO) {
            try {
                val inputStream = contentResolver.openInputStream(uri) ?: return@withContext null
                val storageRef = Firebase.storage.reference.child("chat_images/${UUID.randomUUID()}.jpg")
                storageRef.putStream(inputStream).await()
                inputStream.close()
                storageRef.downloadUrl.await().toString()
            } catch (_: Exception) { null }
        }
    }
}