package com.trio.app.ui.screen

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.trio.app.data.SessionManager
import com.trio.app.data.firebase.FirestoreUser
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_AvatarGradients
import com.trio.app.ui.theme.TG_Background
import com.trio.app.ui.theme.TG_DarkBackground
import com.trio.app.ui.theme.TG_DarkSurface
import com.trio.app.ui.theme.TG_Separator
import com.trio.app.ui.theme.TG_Surface
import com.trio.app.ui.theme.TG_TextSecondary

data class SettingsSection(val title: String, val items: List<SettingsItem>)
data class SettingsItem(val icon: ImageVector, val title: String, val onClick: () -> Unit)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onLogout: () -> Unit, onEditProfile: () -> Unit = {}) {
    val isDark = isSystemInDarkTheme()
    val bg = if (isDark) TG_DarkBackground else TG_Background
    val sectionBg = if (isDark) TG_DarkSurface else TG_Surface
    val divider = if (isDark) Color(0xFF38383A) else TG_Separator
    val userId = SessionManager.currentUser.value?.id ?: return
    var userDoc by remember { mutableStateOf<FirestoreUser?>(null) }
    var loaded by remember { mutableStateOf(false) }

    if (!loaded) {
        Firebase.firestore.collection("users").document(userId).get()
            .addOnSuccessListener { doc ->
                userDoc = FirestoreUser.fromDoc(doc)
                loaded = true
            }
        return
    }

    val currentUsername = userDoc?.username ?: "用户"
    val currentBio = userDoc?.bio
    val avatarGradient = remember(userDoc?.username) {
        val idx = (userDoc?.username?.hashCode() ?: 0).mod(TG_AvatarGradients.size)
        TG_AvatarGradients[idx]
    }

    val sections = listOf(
        SettingsSection("账户", listOf(
            SettingsItem(Icons.Filled.Person, "个人信息") { onEditProfile() },
            SettingsItem(Icons.Filled.Security, "隐私与安全") {},
            SettingsItem(Icons.Filled.Chat, "聊天设置") {}
        )),
        SettingsSection("通知", listOf(
            SettingsItem(Icons.Filled.Notifications, "通知与声音") {}
        )),
        SettingsSection("其他", listOf(
            SettingsItem(Icons.Filled.Brightness4, "深色模式") {},
            SettingsItem(Icons.Filled.Info, "关于 Trio") {}
        ))
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("设置", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = sectionBg)
            )
        },
        containerColor = bg
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding).padding(top = 16.dp)) {
            item {
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(Modifier.size(88.dp).clip(CircleShape).background(brush = Brush.linearGradient(avatarGradient)), contentAlignment = Alignment.Center) {
                        Text(currentUsername.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 34.sp)
                    }
                    Spacer(Modifier.height(12.dp))
                    Text(currentUsername, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                    Spacer(Modifier.height(4.dp))
                    Text(currentBio?.ifBlank { "这个人很懒，什么都没写" } ?: "这个人很懒，什么都没写", fontSize = 13.sp, color = TG_TextSecondary)
                    Spacer(Modifier.height(24.dp))
                }
            }

            sections.forEach { section ->
                item {
                    Text(section.title, fontSize = 13.sp, color = TG_TextSecondary, modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 6.dp))
                }
                item {
                    Column(
                        Modifier.fillMaxWidth().padding(horizontal = 12.dp).clip(RoundedCornerShape(12.dp)).background(sectionBg)
                    ) {
                        section.items.forEachIndexed { index, item ->
                            Row(
                                Modifier.fillMaxWidth().clickable { item.onClick() }.padding(horizontal = 16.dp, vertical = 14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(item.icon, null, tint = TG_ActionBar, modifier = Modifier.size(22.dp))
                                Spacer(Modifier.width(14.dp))
                                Text(item.title, Modifier.weight(1f), fontSize = 16.sp, color = MaterialTheme.colorScheme.onSurface)
                                Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, null, tint = TG_TextSecondary, modifier = Modifier.size(20.dp))
                            }
                            if (index < section.items.size - 1) HorizontalDivider(thickness = 0.5.dp, color = divider, modifier = Modifier.padding(start = 52.dp))
                        }
                    }
                }
            }

            item {
                Box(Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 24.dp).clip(RoundedCornerShape(12.dp)).background(sectionBg)) {
                    Row(
                        Modifier.fillMaxWidth().clickable { onLogout() }.padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Logout, null, tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(22.dp))
                        Spacer(Modifier.width(14.dp))
                        Text("退出登录", Modifier.weight(1f), fontSize = 16.sp, color = MaterialTheme.colorScheme.error)
                    }
                }
            }

            item { Spacer(Modifier.height(32.dp)) }
        }
    }
}
