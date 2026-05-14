package com.trio.app.ui.screen

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.trio.app.data.SessionManager
import com.trio.app.data.firebase.FirestoreUser
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_AvatarGradients
import com.trio.app.ui.theme.TG_Background
import com.trio.app.ui.theme.TG_DarkBackground
import com.trio.app.ui.theme.TG_DarkSurface
import com.trio.app.ui.theme.TG_Surface
import com.trio.app.ui.theme.TG_TextSecondary
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(onBack: () -> Unit) {
    val isDark = isSystemInDarkTheme()
    val bg = if (isDark) TG_DarkBackground else TG_Background
    val cardBg = if (isDark) TG_DarkSurface else TG_Surface
    val context = LocalContext.current
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

    var username by remember(userDoc) { mutableStateOf(userDoc?.username ?: "") }
    var bio by remember(userDoc) { mutableStateOf(userDoc?.bio ?: "") }
    var avatarUrl by remember(userDoc) { mutableStateOf(userDoc?.avatar ?: "") }
    var isSaving by remember { mutableStateOf(false) }
    var showAvatarDialog by remember { mutableStateOf(false) }

    val avatarGradient = remember(userDoc?.username) {
        val idx = (userDoc?.username?.hashCode() ?: 0).mod(TG_AvatarGradients.size)
        TG_AvatarGradients[idx]
    }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val inputStream = context.contentResolver.openInputStream(it) ?: return@launch
                    val storageRef = Firebase.storage.reference.child("avatars/${UUID.randomUUID()}.jpg")
                    storageRef.putStream(inputStream).await()
                    inputStream.close()
                    val url = storageRef.downloadUrl.await().toString()
                    withContext(Dispatchers.Main) { avatarUrl = url }
                } catch (_: Exception) {}
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回")
                    }
                },
                title = { Text("编辑资料", fontWeight = FontWeight.Bold) },
                actions = {
                    TextButton(
                        onClick = {
                            isSaving = true
                            CoroutineScope(Dispatchers.IO).launch {
                                try {
                                    val updates = mutableMapOf<String, Any>()
                                    if (username.isNotBlank()) updates["username"] = username
                                    if (bio.isNotBlank()) updates["bio"] = bio
                                    if (avatarUrl.isNotBlank() && avatarUrl != userDoc?.avatar) updates["avatar"] = avatarUrl
                                    if (updates.isNotEmpty()) {
                                        Firebase.firestore.collection("users").document(userId)
                                            .update(updates).await()
                                    }
                                } catch (_: Exception) {}
                                withContext(Dispatchers.Main) {
                                    isSaving = false
                                    onBack()
                                }
                            }
                        },
                        enabled = !isSaving
                    ) {
                        Text(if (isSaving) "保存中..." else "保存", color = TG_ActionBar, fontWeight = FontWeight.SemiBold)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = cardBg)
            )
        },
        containerColor = bg
    ) { padding ->
        Column(
            Modifier.fillMaxSize().padding(padding).verticalScroll(rememberScrollState()).padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            Box(contentAlignment = Alignment.BottomEnd) {
                Box(
                    Modifier.size(96.dp).clip(CircleShape)
                        .background(brush = Brush.linearGradient(avatarGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    if (avatarUrl.isNotBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(context).data(avatarUrl).crossfade(true).build(),
                            contentDescription = "头像",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text(username.take(1).uppercase().ifBlank { "?" },
                            color = Color.White, fontWeight = FontWeight.Bold, fontSize = 38.sp)
                    }
                }
                Box(
                    Modifier.size(32.dp).clip(CircleShape).background(TG_ActionBar).clickable { showAvatarDialog = true },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Filled.CameraAlt, "换头像", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            Spacer(Modifier.height(36.dp))

            Column(Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(cardBg).padding(horizontal = 16.dp, vertical = 4.dp)) {
                Text("昵称", fontSize = 13.sp, color = TG_TextSecondary, modifier = Modifier.padding(top = 10.dp))
                BasicTextField(
                    value = username,
                    onValueChange = { username = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                    singleLine = true,
                    decorationBox = { inner ->
                        if (username.isBlank()) Text("给自己取个名字", color = TG_TextSecondary, fontSize = 16.sp)
                        inner()
                    }
                )

                Spacer(Modifier.height(12.dp).fillMaxWidth().background(SeparatorFg, RoundedCornerShape(0.5.dp)))

                Text("个性签名", fontSize = 13.sp, color = TG_TextSecondary, modifier = Modifier.padding(top = 10.dp))
                BasicTextField(
                    value = bio,
                    onValueChange = { bio = it },
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    textStyle = TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 16.sp),
                    singleLine = true,
                    decorationBox = { inner ->
                        if (bio.isBlank()) Text("写一句话介绍自己", color = TG_TextSecondary, fontSize = 16.sp)
                        inner()
                    }
                )
            }
        }
    }

    if (showAvatarDialog) {
        AlertDialog(
            onDismissRequest = { showAvatarDialog = false },
            title = { Text("更换头像", fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    Row(
                        Modifier.fillMaxWidth().clickable {
                            showAvatarDialog = false
                            imagePickerLauncher.launch("image/*")
                        }.padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.CameraAlt, null, tint = TG_ActionBar, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("从相册选择", fontSize = 16.sp)
                    }
                }
            },
            confirmButton = {},
            dismissButton = {
                TextButton(onClick = { showAvatarDialog = false }) { Text("取消") }
            }
        )
    }
}

private val SeparatorFg = Color(0xFFE0E0E0)