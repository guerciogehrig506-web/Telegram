package com.trio.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.app.data.SessionManager
import com.trio.app.data.api.ApiClient
import com.trio.app.data.model.ChatPreview
import com.trio.app.data.model.ChatResponse
import com.trio.app.data.model.Contact
import com.trio.app.data.model.OnlineStatus
import com.trio.app.data.model.ReadStatus
import com.trio.app.data.model.User
import com.trio.app.ui.theme.TG_AvatarGradients
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ChatListViewModel : ViewModel() {
    private val _chats = MutableStateFlow<List<ChatPreview>>(emptyList())
    val chats: StateFlow<List<ChatPreview>> = _chats

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    init { loadChats() }

    fun refresh() {
        viewModelScope.launch {
            _isRefreshing.value = true
            loadChats()
            _isRefreshing.value = false
        }
    }

    private fun loadChats() {
        val currentUser = SessionManager.currentUser.value
        if (currentUser == null) {
            _isLoading.value = false
            return
        }
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getChats()
                if (response.isSuccessful) {
                    val chatList = response.body() ?: emptyList()
                    _chats.value = chatList.map { it.toChatPreview() }
                        .sortedByDescending { it.lastMessageTime }
                }
            } catch (_: Exception) {}
            _isLoading.value = false
        }
    }

    private fun ChatResponse.toChatPreview(): ChatPreview {
        val gradIdx = user.username.hashCode().mod(TG_AvatarGradients.size)
        return ChatPreview(
            contact = Contact(
                user = user,
                onlineStatus = OnlineStatus.OFFLINE,
                avatarGradient = TG_AvatarGradients[gradIdx]
            ),
            lastMessage = lastMessage,
            lastMessageTime = lastMessageTime,
            unreadCount = unreadCount,
            readStatus = if (unreadCount > 0) ReadStatus.DELIVERED else ReadStatus.READ
        )
    }
}