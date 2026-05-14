package com.trio.app.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_AvatarGradients
import com.trio.app.ui.theme.TG_Background
import com.trio.app.ui.theme.TG_DarkBackground
import com.trio.app.ui.theme.TG_DarkSurface
import com.trio.app.ui.theme.TG_Surface
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.viewmodel.CreateGroupViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateGroupScreen(onBack: () -> Unit, onGroupCreated: (String) -> Unit) {
    val viewModel: CreateGroupViewModel = viewModel()
    val contacts by viewModel.contacts.collectAsStateWithLifecycle()
    val isDark = isSystemInDarkTheme()
    val bg = if (isDark) TG_DarkBackground else TG_Background
    val cardBg = if (isDark) TG_DarkSurface else TG_Surface
    val context = LocalContext.current
    val selectedIds = remember { mutableStateListOf<String>() }
    var groupName by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadContacts() }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = { IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回") } },
                title = { Text("新建群聊", fontWeight = FontWeight.Bold) },
                actions = {
                    Text(
                        "创建",
                        color = if (selectedIds.size >= 2 && groupName.isNotBlank()) TG_ActionBar else TG_TextSecondary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        modifier = Modifier.clickable(enabled = selectedIds.size >= 2 && groupName.isNotBlank()) {
                            viewModel.createGroup(groupName, selectedIds.toList()) {
                                Toast.makeText(context, "群聊创建成功", Toast.LENGTH_SHORT).show()
                                onGroupCreated(it)
                            }
                        }.padding(horizontal = 12.dp)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg)
            )
        },
        containerColor = bg
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            BasicTextField(
                value = groupName,
                onValueChange = { groupName = it },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).clip(RoundedCornerShape(12.dp)).background(cardBg).padding(16.dp),
                textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                singleLine = true,
                decorationBox = { inner ->
                    if (groupName.isBlank()) Text("输入群聊名称", color = TG_TextSecondary, fontSize = 16.sp)
                    inner()
                }
            )

            Spacer(Modifier.height(4.dp))
            Text("选择成员（至少2人）", Modifier.padding(horizontal = 16.dp, vertical = 4.dp), fontSize = 13.sp, color = TG_TextSecondary)

            LazyColumn(Modifier.fillMaxSize()) {
                items(contacts, key = { it.id }) { user ->
                    val isSelected = selectedIds.contains(user.id)
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            if (isSelected) selectedIds.remove(user.id) else selectedIds.add(user.id)
                        }.padding(horizontal = 12.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (isSelected) TG_ActionBar.copy(alpha = 0.1f) else Color.Transparent)
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            Modifier.size(42.dp).clip(CircleShape).background(brush = Brush.linearGradient(TG_AvatarGradients[user.username.hashCode().mod(TG_AvatarGradients.size)])),
                            contentAlignment = Alignment.Center
                        ) {
                            if (user.avatar.isNotBlank()) AsyncImage(
                                model = ImageRequest.Builder(context).data(user.avatar).crossfade(true).build(),
                                contentDescription = null, modifier = Modifier.fillMaxSize()
                            ) else Text(user.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(user.username, fontWeight = FontWeight.Medium, fontSize = 15.sp)
                            if (user.bio.isNotBlank()) Text(user.bio, fontSize = 12.sp, color = TG_TextSecondary)
                        }
                        if (isSelected) Text("✓", color = TG_ActionBar, fontWeight = FontWeight.Bold, fontSize = 20.sp, modifier = Modifier.padding(end = 8.dp))
                    }
                }
            }
        }
    }
}