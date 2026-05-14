package com.trio.app.ui.screen

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.KeyboardArrowDown
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.core.content.FileProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.trio.app.data.SessionManager
import com.trio.app.data.SharedData
import com.trio.app.data.model.Contact
import com.trio.app.data.model.User
import com.trio.app.ui.animation.TrioAnimation
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_ActionBarDark
import com.trio.app.ui.theme.TG_BubbleIncoming
import com.trio.app.ui.theme.TG_BubbleOutgoing
import com.trio.app.ui.theme.TG_Separator
import com.trio.app.ui.theme.TG_Surface
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.viewmodel.ChatViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

data class ChatMessage(
    val id: String,
    val sender: User,
    val content: String,
    val image: String? = null,
    val timestamp: Long,
    val isSent: Boolean,
    val isRead: Boolean = false
)

data class DisplayItem(
    val isDate: Boolean,
    val dateLabel: String = "",
    val message: ChatMessage? = null,
    val showSender: Boolean = false,
    val senderContact: Contact? = null
)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(userId: String, onBack: (() -> Unit)? = null, chatViewModel: ChatViewModel = viewModel()) {
    LaunchedEffect(userId) { chatViewModel.init(userId) }

    val messages by chatViewModel.messages.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    LaunchedEffect(messages) {}

    val contact = remember(userId) { SharedData.getContact(userId) }
    val chatUser = contact.user
    val avatarGradient = contact.avatarGradient

    val currentUser = SessionManager.currentUser.collectAsState().value
        ?: User("self", "我", "", "USER", isActive = true, createdAt = "", updatedAt = "")

    val selfContact = remember {
        Contact(currentUser, com.trio.app.data.model.OnlineStatus.ONLINE,
            listOf(Color(0xFF5EB6FB), Color(0xFF1FCEEB)))
    }

    var messageText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()
    val context = LocalContext.current
    val isUploading by chatViewModel.isUploading.collectAsState()
    var previewImage by remember { mutableStateOf<String?>(null) }
    var showImagePickerDialog by remember { mutableStateOf(false) }

    val cameraUri = remember { mutableStateOf<Uri?>(null) }

    var isSearching by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var searchMatchIndex by remember { mutableStateOf(0) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { chatViewModel.sendImage(it, context.contentResolver) }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraUri.value?.let { chatViewModel.sendImage(it, context.contentResolver) }
        }
    }

    val hasTyped by remember { mutableStateOf(false) }

    val displayItems = remember(messages, searchQuery) {
        val base = buildDisplayItems(messages, contact, selfContact)
        if (searchQuery.isBlank()) base
        else base.filter { item ->
            item.isDate || (item.message?.content?.contains(searchQuery, ignoreCase = true) == true)
        }
    }

    val searchMatchCount = remember(displayItems) {
        displayItems.count { !it.isDate && it.message?.content?.contains(searchQuery, ignoreCase = true) == true }
    }

    LaunchedEffect(searchQuery) { searchMatchIndex = 0 }

    LaunchedEffect(displayItems.size) {
        if (displayItems.isNotEmpty() && searchQuery.isBlank()) listState.animateScrollToItem(displayItems.size - 1)
    }

    LaunchedEffect(messageText) {
    }

    Scaffold(
        topBar = {
            if (isSearching) {
                TopAppBar(
                    navigationIcon = {
                        IconButton(onClick = { isSearching = false; searchQuery = "" }) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White)
                        }
                    },
                    title = {
                        Surface(
                            Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White.copy(alpha = 0.2f)
                        ) {
                            Row(Modifier.padding(horizontal = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                BasicTextField(
                                    value = searchQuery,
                                    onValueChange = { searchQuery = it },
                                    modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                                    textStyle = TextStyle(color = Color.White, fontSize = 15.sp),
                                    singleLine = true,
                                    decorationBox = { inner ->
                                        if (searchQuery.isBlank()) Text("搜索消息...", color = Color.White.copy(alpha = 0.6f), fontSize = 15.sp)
                                        inner()
                                    }
                                )
                                if (searchQuery.isNotBlank()) {
                                    IconButton(onClick = { searchQuery = "" }, modifier = Modifier.size(20.dp)) {
                                        Icon(Icons.Filled.Close, "清除", tint = Color.White.copy(alpha = 0.7f), modifier = Modifier.size(18.dp))
                                    }
                                }
                            }
                        }
                    },
                    actions = {
                        if (searchMatchCount > 0) {
                            Text("${searchMatchIndex + 1}/$searchMatchCount", color = Color.White.copy(alpha = 0.8f), fontSize = 13.sp)
                            IconButton(onClick = {
                                if (searchMatchIndex > 0) searchMatchIndex--
                            }) {
                                Icon(Icons.Filled.KeyboardArrowUp, "上一个", tint = Color.White)
                            }
                            IconButton(onClick = {
                                if (searchMatchIndex < searchMatchCount - 1) searchMatchIndex++
                            }) {
                                Icon(Icons.Filled.KeyboardArrowDown, "下一个", tint = Color.White)
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = TG_ActionBar, titleContentColor = Color.White)
                )
            } else {
                TopAppBar(
                    navigationIcon = {
                        if (onBack != null) IconButton(onClick = onBack) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, "返回", tint = Color.White)
                        }
                    },
                    title = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                Modifier.size(32.dp).clip(CircleShape).background(brush = Brush.linearGradient(avatarGradient)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(chatUser.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(Modifier.width(8.dp))
                            Column {
                                Text(chatUser.username, fontWeight = FontWeight.SemiBold, fontSize = 16.sp, color = Color.White)
                                Text("离线",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.8f)
                                )
                            }
                        }
                    },
                    actions = {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Filled.Search, "搜索", tint = Color.White)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = TG_ActionBar, titleContentColor = Color.White)
                )
            }
        },
        containerColor = TG_Separator.copy(alpha = 0.3f)
    ) { paddingValues ->
        Column(Modifier.fillMaxSize().padding(paddingValues)) {
            if (isLoading) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text("加载中...", color = TG_TextSecondary, fontSize = 15.sp)
                }
            } else if (displayItems.isEmpty()) {
                Box(Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("暂无消息", color = TG_TextSecondary, fontSize = 15.sp)
                        Text("发送一条消息开始聊天", color = TG_TextSecondary.copy(alpha = 0.6f), fontSize = 13.sp, modifier = Modifier.padding(top = 4.dp))
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    state = listState,
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    itemsIndexed(displayItems, key = { _, item ->
                        if (item.isDate) "date_${item.dateLabel}"
                        else "msg_${item.message?.id}"
                    }) { _, item ->
                        if (item.isDate) {
                            DateSeparator(item.dateLabel)
                        } else {
                            val msg = item.message!!
                            val isOwn = msg.sender.id == currentUser.id
                            AnimatedVisibility(
                                visible = true,
                                enter = TrioAnimation.messageEnter
                            ) {
                                MessageBubble(
                                    message = msg,
                                    isOwn = isOwn,
                                    showSender = item.showSender,
                                    senderContact = if (!isOwn && item.showSender) contact else null,
                                    onImageClick = { previewImage = it },
                                    highlightQuery = searchQuery,
                                    onLongClick = {
                                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                        clipboard.setPrimaryClip(ClipData.newPlainText("message", msg.content))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            Surface(Modifier.fillMaxWidth(), shadowElevation = 2.dp, color = TG_Surface) {
                Row(Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = {}) { Icon(Icons.Filled.EmojiEmotions, "表情", tint = TG_TextSecondary, modifier = Modifier.size(24.dp)) }
                    IconButton(onClick = { showImagePickerDialog = true }) { Icon(Icons.Filled.AttachFile, "图片", tint = TG_TextSecondary, modifier = Modifier.size(24.dp)) }

                    Surface(Modifier.weight(1f), shape = RoundedCornerShape(22.dp), color = TG_Separator.copy(alpha = 0.5f)) {
                        BasicTextField(
                            value = messageText,
                            onValueChange = { messageText = it },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp),
                            textStyle = androidx.compose.ui.text.TextStyle(color = MaterialTheme.colorScheme.onSurface, fontSize = 15.sp),
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                            maxLines = 3,
                            decorationBox = { inner ->
                                if (messageText.isBlank()) Text("输入消息...", color = TG_TextSecondary, fontSize = 15.sp)
                                inner()
                            }
                        )
                    }

                    IconButton(onClick = {
                        if (messageText.isNotBlank()) {
                            chatViewModel.sendMessage(messageText.trim())
                            messageText = ""
                        }
                    }) {
                        Icon(if (messageText.isBlank()) Icons.Filled.Mic else Icons.Filled.Send, "发送", tint = TG_ActionBar, modifier = Modifier.size(24.dp))
                    }
                }
            }
        }

        if (isUploading) {
            Box(
                Modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = TG_Surface.copy(alpha = 0.9f),
                    shadowElevation = 2.dp
                ) {
                    Row(
                        Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        CircularProgressIndicator(Modifier.size(16.dp), strokeWidth = 2.dp, color = TG_ActionBar)
                        Spacer(Modifier.width(8.dp))
                        Text("图片上传中...", fontSize = 13.sp, color = TG_TextSecondary)
                    }
                }
            }
        }

        if (previewImage != null) {
            Dialog(
                onDismissRequest = { previewImage = null },
                properties = DialogProperties(usePlatformDefaultWidth = false)
            ) {
                Box(
                    Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.95f)).clickable { previewImage = null },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(previewImage)
                            .crossfade(true)
                            .build(),
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
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                showImagePickerDialog = false
                                imagePickerLauncher.launch("image/*")
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.PhotoLibrary, null, tint = TG_ActionBar, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(16.dp))
                            Text("从相册选择", fontSize = 16.sp)
                        }
                        Row(
                            Modifier.fillMaxWidth().clickable {
                                showImagePickerDialog = false
                                val file = File(context.cacheDir, "camera_${System.currentTimeMillis()}.jpg")
                                val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
                                cameraUri.value = uri
                                cameraLauncher.launch(uri)
                            }.padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Filled.CameraAlt, null, tint = TG_ActionBar, modifier = Modifier.size(28.dp))
                            Spacer(Modifier.width(16.dp))
                            Text("拍照", fontSize = 16.sp)
                        }
                    }
                },
                confirmButton = {},
                dismissButton = {
                    TextButton(onClick = { showImagePickerDialog = false }) {
                        Text("取消")
                    }
                }
            )
        }
    }
}

@Composable
fun DateSeparator(label: String) {
    Box(Modifier.fillMaxWidth().padding(vertical = 10.dp), contentAlignment = Alignment.Center) {
        Surface(shape = RoundedCornerShape(10.dp), color = TG_Surface.copy(alpha = 0.85f), shadowElevation = 0.dp) {
            Text(label, fontSize = 13.sp, color = TG_TextSecondary, modifier = Modifier.padding(horizontal = 14.dp, vertical = 4.dp))
        }
    }
}

@Composable
fun MessageBubble(
    message: ChatMessage,
    isOwn: Boolean,
    showSender: Boolean,
    senderContact: Contact?,
    onLongClick: () -> Unit,
    onImageClick: (String) -> Unit = {},
    highlightQuery: String = ""
) {
    val timeStr = remember(message.timestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 1.dp),
        horizontalAlignment = if (isOwn) Alignment.End else Alignment.Start
    ) {
        if (showSender && senderContact != null && !isOwn) {
            Row(
                Modifier.padding(start = 10.dp, bottom = 2.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    Modifier.size(24.dp).clip(CircleShape).background(brush = Brush.linearGradient(senderContact.avatarGradient)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(senderContact.user.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                }
                Spacer(Modifier.width(6.dp))
                Text(senderContact.user.username, fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = TG_ActionBarDark)
            }
        }

        Row(verticalAlignment = Alignment.Bottom) {
            Spacer(if (isOwn) Modifier.weight(0.3f) else Modifier.width(if (showSender) 0.dp else 34.dp))

            Surface(
                shape = if (isOwn) RoundedCornerShape(16.dp, 16.dp, 4.dp, 16.dp)
                else RoundedCornerShape(16.dp, 16.dp, 16.dp, 4.dp),
                color = if (isOwn) TG_BubbleOutgoing else TG_BubbleIncoming,
                shadowElevation = if (isOwn) 0.dp else 0.5.dp,
                modifier = Modifier.combinedClickable(onClick = {}, onLongClick = onLongClick)
            ) {
                Column {
                    if (!message.image.isNullOrBlank()) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(message.image)
                                .crossfade(true)
                                .build(),
                            contentDescription = "图片",
                            modifier = Modifier
                                .widthIn(max = 240.dp)
                                .aspectRatio(4f / 3f)
                                .clip(RoundedCornerShape(if (isOwn) 12.dp else 12.dp))
                                .clickable { onImageClick(message.image!!) },
                            contentScale = ContentScale.Crop
                        )
                    }
                    if (message.content.isNotEmpty()) {
                        Row(
                            Modifier.padding(start = 12.dp, end = 8.dp, top = if (message.image != null) 6.dp else 7.dp, bottom = 7.dp),
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text(
                                buildHighlightedText(message.content, highlightQuery),
                                color = Color(0xFF0A0A0A),
                                fontSize = 15.sp,
                                lineHeight = 21.sp,
                                modifier = Modifier.widthIn(max = 240.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(timeStr, fontSize = 11.sp, color = TG_TextSecondary)
                        }
                    }
                }
            }

            if (isOwn) Spacer(Modifier.width(34.dp))
        }
    }
}

fun buildDisplayItems(
    messages: List<ChatMessage>,
    chatContact: Contact,
    selfContact: Contact
): List<DisplayItem> {
    if (messages.isEmpty()) return emptyList()

    val result = mutableListOf<DisplayItem>()
    var lastDate = ""
    var lastSenderId = ""
    val cal = Calendar.getInstance()

    messages.forEach { msg ->
        cal.timeInMillis = msg.timestamp
        val dateLabel = formatDateLabel(cal)
        if (dateLabel != lastDate) {
            result.add(DisplayItem(isDate = true, dateLabel = dateLabel))
            lastDate = dateLabel
            lastSenderId = ""
        }

        val isOwn = msg.sender.id == SessionManager.currentUser.value?.id
        val showSender = !isOwn && msg.sender.id != lastSenderId
        val contact = if (isOwn) selfContact else chatContact

        result.add(DisplayItem(
            isDate = false,
            message = msg,
            showSender = showSender,
            senderContact = contact
        ))

        lastSenderId = msg.sender.id
    }

    return result
}

fun formatDateLabel(cal: Calendar): String {
    val today = Calendar.getInstance()
    return when {
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) -> "今天"
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
        cal.get(Calendar.DAY_OF_YEAR) == today.get(Calendar.DAY_OF_YEAR) - 1 -> "昨天"
        cal.get(Calendar.YEAR) == today.get(Calendar.YEAR) -> "${cal.get(Calendar.MONTH) + 1}月${cal.get(Calendar.DAY_OF_MONTH)}日"
        else -> "${cal.get(Calendar.YEAR)}年${cal.get(Calendar.MONTH) + 1}月${cal.get(Calendar.DAY_OF_MONTH)}日"
    }
}

fun buildHighlightedText(text: String, query: String): AnnotatedString {
    if (query.isBlank()) return AnnotatedString(text)
    return buildAnnotatedString {
        var start = 0
        val lowerText = text.lowercase(Locale.getDefault())
        val lowerQuery = query.lowercase(Locale.getDefault())
        while (true) {
            val idx = lowerText.indexOf(lowerQuery, start)
            if (idx == -1) {
                append(text.substring(start))
                break
            }
            append(text.substring(start, idx))
            withStyle(SpanStyle(background = Color(0xFFFFD54F), color = Color.Black)) {
                append(text.substring(idx, idx + query.length))
            }
            start = idx + query.length
        }
    }
}