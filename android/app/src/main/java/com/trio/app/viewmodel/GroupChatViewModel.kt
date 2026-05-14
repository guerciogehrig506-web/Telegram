package com.trio.app.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.storage.ktx.storage
import com.google.firebase.ktx.Firebase
import com.trio.app.data.SessionManager
import com.trio.app.data.api.ApiClient
import com.trio.app.data.api.SendMessageRequest
import com.trio.app.data.model.Group
import com.trio.app.data.model.Message
import com.trio.app.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

class GroupChatViewModel : ViewModel() {
    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages

    private val _group = MutableStateFlow<Group?>(null)
    val group: StateFlow<Group?> = _group

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private var currentGroupId: String = ""

    fun loadGroup(groupId: String) {
        currentGroupId = groupId
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getGroupById(groupId)
                if (response.isSuccessful) {
                    _group.value = response.body()
                }
            } catch (_: Exception) {}
        }
        listenForMessages()
    }

    fun listenForMessages() {
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getGroupMessages(currentGroupId)
                if (response.isSuccessful) {
                    _messages.value = response.body() ?: emptyList()
                }
            } catch (_: Exception) {}
            _isLoading.value = false
        }
    }

    fun sendMessage(content: String, senderId: String) {
        val currentUser = SessionManager.currentUser.value ?: return
        viewModelScope.launch {
            try {
                ApiClient.apiService.sendMessage(
                    SendMessageRequest(content = content, type = "text", groupId = currentGroupId)
                )
                listenForMessages()
            } catch (_: Exception) {}
        }
    }

    fun sendImage(uri: Uri, contentResolver: android.content.ContentResolver) {
        viewModelScope.launch {
            _isUploading.value = true
            try {
                val imageUrl = withContext(Dispatchers.IO) {
                    val inputStream = contentResolver.openInputStream(uri) ?: return@withContext null
                    val storageRef = Firebase.storage.reference.child("group_images/${UUID.randomUUID()}.jpg")
                    storageRef.putStream(inputStream).await()
                    inputStream.close()
                    storageRef.downloadUrl.await().toString()
                }
                if (imageUrl != null) {
                    ApiClient.apiService.sendMessage(
                        SendMessageRequest(content = "[图片]", type = "image", groupId = currentGroupId)
                    )
                    listenForMessages()
                }
            } catch (_: Exception) {}
            _isUploading.value = false
        }
    }
}