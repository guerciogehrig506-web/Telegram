package com.trio.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trio.app.data.SessionManager
import com.trio.app.data.api.ApiClient
import com.trio.app.data.model.Contact
import com.trio.app.data.model.OnlineStatus
import com.trio.app.data.model.User
import com.trio.app.ui.theme.TG_AvatarGradients
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ContactGroup(val name: String, val contacts: List<Contact>)

class ContactsViewModel : ViewModel() {
    private val _groups = MutableStateFlow<List<ContactGroup>>(emptyList())
    val groups: StateFlow<List<ContactGroup>> = _groups

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init { loadContacts() }

    fun refresh() { viewModelScope.launch { loadContacts() } }

    private fun loadContacts() {
        val currentUser = SessionManager.currentUser.value
        if (currentUser == null) {
            _isLoading.value = false
            return
        }
        viewModelScope.launch {
            try {
                val response = ApiClient.apiService.getUsers()
                if (response.isSuccessful) {
                    val allUsers = response.body() ?: emptyList()
                    val users = allUsers.filter { it.id != currentUser.id }.map { user ->
                        Contact(
                            user = user,
                            onlineStatus = OnlineStatus.OFFLINE,
                            avatarGradient = TG_AvatarGradients[user.username.hashCode().mod(TG_AvatarGradients.size)]
                        )
                    }

                    val query = _searchQuery.value
                    val filtered = if (query.isBlank()) users
                    else users.filter { it.user.username.contains(query, ignoreCase = true) ||
                        it.user.email.contains(query, ignoreCase = true) }

                    _groups.value = filtered
                        .groupBy { contact ->
                            val first = contact.user.username.firstOrNull() ?: '#'
                            if (first.isLetter()) first.uppercase() else "#"
                        }
                        .map { (key, members) -> ContactGroup(key, members) }
                        .sortedBy { it.name }
                }
            } catch (_: Exception) {}
            _isLoading.value = false
        }
    }

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
        loadContacts()
    }
}