package com.trio.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.trio.app.data.SessionManager
import com.trio.app.data.firebase.FirestoreUser
import com.trio.app.data.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class CreateGroupViewModel : ViewModel() {
    private val _contacts = MutableStateFlow<List<User>>(emptyList())
    val contacts: StateFlow<List<User>> = _contacts

    private val _isCreating = MutableStateFlow(false)
    val isCreating: StateFlow<Boolean> = _isCreating

    fun loadContacts() {
        viewModelScope.launch {
            try {
                val currentId = SessionManager.currentUser.value?.id
                val snapshot = Firebase.firestore.collection("users").get().await()
                _contacts.value = snapshot.documents.mapNotNull { doc ->
                    val u = FirestoreUser.fromDoc(doc)
                    if (u.id == currentId) null
                    else User(u.id, u.username, u.email, u.role, bio = u.bio, avatar = u.avatar, isActive = u.isActive, createdAt = "", updatedAt = "")
                }
            } catch (_: Exception) {}
        }
    }

    fun createGroup(name: String, memberIds: List<String>, onSuccess: (String) -> Unit) {
        viewModelScope.launch {
            _isCreating.value = true
            try {
                val allMembers = (memberIds + SessionManager.currentUser.value?.id!!).distinct()
                val ref = Firebase.firestore.collection("groups").add(
                    mapOf(
                        "name" to name,
                        "avatar" to "",
                        "members" to allMembers,
                        "createdAt" to System.currentTimeMillis()
                    )
                ).await()
                onSuccess(ref.id)
            } catch (_: Exception) {}
            _isCreating.value = false
        }
    }
}