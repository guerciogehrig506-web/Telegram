package com.trio.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.trio.app.ui.animation.AnimatedListItem
import com.trio.app.ui.components.ContactListItem
import com.trio.app.ui.components.ContactSkeletonItem
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.viewmodel.ContactsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactsScreen(onContactClick: (String) -> Unit, viewModel: ContactsViewModel = viewModel()) {
    val groups by viewModel.groups.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val isLoading by viewModel.isLoading.collectAsStateWithLifecycle()

    val filteredGroups = if (searchQuery.isBlank()) groups else groups.mapNotNull { g ->
        val f = g.contacts.filter { it.user.username.contains(searchQuery, true) || it.user.department.contains(searchQuery, true) }
        if (f.isNotEmpty()) g.copy(contacts = f) else null
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("通讯录", fontWeight = FontWeight.Bold) }, colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.surface)) },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            OutlinedTextField(
                searchQuery, viewModel::onSearchQueryChange,
                Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("搜索联系人...", color = TG_TextSecondary) },
                leadingIcon = { Icon(Icons.Filled.Search, "搜索", tint = TG_TextSecondary) },
                singleLine = true, shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                    unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
                    focusedBorderColor = TG_ActionBar,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
            )

            if (isLoading) {
                Column(Modifier.fillMaxSize()) {
                    repeat(5) { ContactSkeletonItem() }
                }
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 8.dp)) {
                    filteredGroups.forEach { group ->
                        item(key = "h_${group.name}") {
                            Text(group.name, Modifier.fillMaxWidth().background(MaterialTheme.colorScheme.background).padding(horizontal = 16.dp, vertical = 8.dp), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = TG_TextSecondary, fontSize = 13.sp)
                        }
                        itemsIndexed(group.contacts, key = { _, contact -> "c_${contact.user.id}" }) { index, contact ->
                            AnimatedListItem(index = index) {
                                ContactListItem(contact) { onContactClick(contact.user.id) }
                            }
                        }
                    }
                }
            }
        }
    }
}
