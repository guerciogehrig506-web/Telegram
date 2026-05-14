package com.trio.app.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.trio.app.data.model.Group
import com.trio.app.ui.animation.TrioAnimation
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_AvatarGradients
import com.trio.app.ui.theme.TG_Background
import com.trio.app.ui.theme.TG_BubbleIncoming
import com.trio.app.ui.theme.TG_BubbleOutgoing
import com.trio.app.ui.theme.TG_DarkBackground
import com.trio.app.ui.theme.TG_DarkSurface
import com.trio.app.ui.theme.TG_Separator
import com.trio.app.ui.theme.TG_Surface
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.viewmodel.GroupChatViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun GroupChatScreen(groupId: String, onBack: () -> Unit) {
    val viewModel: GroupChatViewModel = viewModel()
    val messages by viewModel.messages.collectAsState()
    val isUploading by viewModel.isUploading.collectAsState()
    val group by viewModel.group.collectAsState()
    val isDark = isSystemInDarkTheme()
    val bg = if (isDark) TG_DarkBackground else TG_Background
    val myId = com.trio.app.data.SessionManager.currentUser.collectAsState().value?.id ?: ""

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    var previewImage by remember { mutableStateOf<String?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }
    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let { viewModel.sendImage(it, context.contentResolver) }
    }
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) cameraUri.value?.let { viewModel.sendImage(it, context.contentResolver) }
    }

    LaunchedEffect(groupId) { viewModel.loadGroup(groupId) }

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.size - 1)
    }

    val avatarGradient = remember(group?.name) {
        val idx = (group?.name?.hashCode() ?: 0).mod(TG_AvatarGradients.size)
        TG_AvatarGradients[idx]
    }

    Scaffold(
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White)
                    }
                },
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier.size(32.dp).clip(CircleShape).background(brush = Brush.linearGradient(avatarGradient)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Filled.Group, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text(group?.name ?: "群聊", fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                            Text("${group?.members?.size ?: 0} 名成员", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = TG_ActionBar, titleContentColor = Color.White)
            )
        },
        containerColor = bg
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(
                Modifier.weight(1f).fillMaxWidth(),
                state = listState,
                reverseLayout = false
            ) {
                itemsIndexed(messages, key = { _, m -> m.id }) { _, msg ->
                    val isOwn = msg.senderId == myId
                    AnimatedVisibility(
                        visible = true,
                        enter = TrioAnimation.messageEnter
                    ) {
                        Box(Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 2.dp),
                            contentAlignment = if (isOwn) Alignment.CenterEnd else Alignment.CenterStart) {
                            Column(horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start) {
                                if (!isOwn) {
                                    Text(
                                        msg.sender?.username ?: "",
                                        fontSize = 12.sp,
                                        color = TG_TextSecondary,
                                        modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
                                    )
                                }
                                Column(
                                    Modifier.clip(RoundedCornerShape(if (isOwn) 16.dp else 4.dp, 16.dp, 16.dp, 16.dp))
                                        .background(if (isOwn) TG_BubbleOutgoing else TG_BubbleIncoming)
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .let { if (!isOwn) it.background(TG_BubbleIncoming).padding(vertical = 8.dp) else it.padding(vertical = 8.dp) }
                                        .combinedClickable(
                                            onClick = {},
                                            onLongClick = {
                                                val cm = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                cm.setPrimaryClip(ClipData.newPlainText("message", msg.content))
                                        }
                                    )
                            ) {
                                if (msg.type == "image" && msg.image != null) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context).data(msg.image).crossfade(true).build(),
                                        contentDescription = "图片",
                                        modifier = Modifier.widthIn(max = 240.dp).aspectRatio(4f / 3f).clip(RoundedCornerShape(8.dp)).clickable { previewImage = msg.image },
                                        contentScale = ContentScale.Crop
                                    )
                                }
                                if (msg.content.isNotBlank()) {
                                    Text(msg.content, color = Color(0xFF0A0A0A), fontSize = 15.sp, lineHeight = 21.sp, modifier = Modifier.widthIn(max = 240.dp))
                                }
                                Row(Modifier.padding(top = 2.dp), horizontalArrangement = Arrangement.End) {
                                    if (isOwn && msg.isRead) {
                                        Text("✓✓", fontSize = 10.sp, color = Color(0xFF4FC3F7), modifier = Modifier.padding(end = 4.dp))
                                    } else if (isOwn) {
                                        Text("✓", fontSize = 10.sp, color = TG_TextSecondary, modifier = Modifier.padding(end = 4.dp))
                                    }
                                    Text(
                                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(msg.createdAt.toLong())),
                                        fontSize = 10.sp, color = TG_TextSecondary
                                    )
                                }
                            }
                        }
                    }
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = {}) { Icon(Icons.Filled.EmojiEmotions, "表情", tint = TG_TextSecondary, modifier = Modifier.size(24.dp)) }
                IconButton(onClick = { showImagePickerDialog = true }) { Icon(Icons.Filled.AttachFile, "图片", tint = TG_TextSecondary, modifier = Modifier.size(24.dp)) }
                Surface(
                    Modifier.weight(1f).clip(RoundedCornerShape(20.dp)),
                    color = if (isDark) TG_DarkSurface else TG_Surface
                ) {
                    BasicTextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 10.dp),
                        textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                        decorationBox = { inner ->
                            if (messageText.isBlank()) Text("消息", color = TG_TextSecondary, fontSize = 15.sp)
                            inner()
                        }
                    )
                }
                Spacer(Modifier.width(4.dp))
                IconButton(onClick = {
                    if (messageText.isNotBlank()) {
                        viewModel.sendMessage(messageText, myId)
                        messageText = ""
                    }
                }) {
                    Icon(Icons.Filled.Send, "发送", tint = if (messageText.isNotBlank()) TG_ActionBar else TG_TextSecondary, modifier = Modifier.size(24.dp))
                }
            }
        }
    }

    if (previewImage != null) {
        Dialog(onDismissRequest = { previewImage = null }, properties = DialogProperties(usePlatformDefaultWidth = false)) {
            Box(Modifier.fillMaxSize().background(Color.Black).clickable { previewImage = null }, contentAlignment = Alignment.Center) {
                AsyncImage(
                    model = ImageRequest.Builder(context).data(previewImage).crossfade(true).build(),
                    contentDescription = "预览",
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    contentScale = ContentScale.Fit
                )
            }
        }
    }

    if (showImagePickerDialog) {
        AlertDialog(
            onDismissRequest = { showImagePickerDialog = false },
            title = { Text("选择图片", fontWeight = FontWeight.SemiBold) },
            text = {
                Column {
                    Row(Modifier.fillMaxWidth().clickable { showImagePickerDialog = false; imagePickerLauncher.launch("image/*") }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.PhotoLibrary, null, tint = TG_ActionBar, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("从相册选择", fontSize = 16.sp)
                    }
                    Row(Modifier.fillMaxWidth().clickable {
                        showImagePickerDialog = false
                        val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                        val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                        cameraUri.value = uri
                        cameraLauncher.launch(uri)
                    }.padding(vertical = 12.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Filled.CameraAlt, null, tint = TG_ActionBar, modifier = Modifier.size(28.dp))
                        Spacer(Modifier.width(16.dp))
                        Text("拍照", fontSize = 16.sp)
                    }
                }
            },
            confirmButton = {},
            dismissButton = { TextButton(onClick = { showImagePickerDialog = false }) { Text("取消") } }
        )
    }
}