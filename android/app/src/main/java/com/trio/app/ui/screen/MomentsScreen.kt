package com.trio.app.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.trio.app.data.SessionManager
import com.trio.app.data.firebase.FirestoreMoment
import com.trio.app.ui.theme.TG_AvatarGradients
import com.trio.app.ui.theme.TG_DarkBackground
import com.trio.app.ui.theme.TG_DarkSurface
import com.trio.app.ui.theme.TG_Background
import com.trio.app.ui.theme.TG_MomentsLike
import com.trio.app.ui.theme.TG_Surface
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.ui.animation.AnimatedListItem
import com.trio.app.ui.components.MomentSkeletonItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MomentsScreen() {
    val isDark = isSystemInDarkTheme()
    val bg = if (isDark) TG_DarkBackground else TG_Background
    val currentUserId = SessionManager.currentUser.value?.id

    var moments by remember { mutableStateOf(listOf<FirestoreMoment>()) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        Firebase.firestore.collection("moments")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { snapshot ->
                moments = snapshot.documents.mapNotNull { doc ->
                    FirestoreMoment.fromDoc(doc, currentUserId)
                }
                isLoading = false
            }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("动态", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = if (isDark) TG_DarkSurface else TG_Surface)
            )
        },
        containerColor = bg
    ) { padding ->
        if (isLoading) {
            Column(Modifier.fillMaxSize().padding(padding)) {
                repeat(4) { MomentSkeletonItem() }
            }
        } else {
            LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(bottom = 16.dp)) {
                itemsIndexed(moments, key = { _, moment -> moment.id }) { index, moment ->
                    AnimatedListItem(index = index) {
                        MomentCard(moment) { updated ->
                            moments = moments.map { if (it.id == updated.id) updated else it }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MomentCard(moment: FirestoreMoment, onUpdate: (FirestoreMoment) -> Unit) {
    val isDark = isSystemInDarkTheme()
    val cardBg = if (isDark) TG_DarkSurface else TG_Surface
    val gradient = remember(moment.username) {
        TG_AvatarGradients[moment.username.hashCode().mod(TG_AvatarGradients.size)]
    }
    val currentUserId = SessionManager.currentUser.value?.id

    Column(
        Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(14.dp)).background(cardBg)
    ) {
        Row(Modifier.padding(14.dp)) {
            Box(Modifier.size(42.dp).clip(CircleShape)
                .background(brush = Brush.linearGradient(gradient)),
                contentAlignment = Alignment.Center
            ) {
                Text(moment.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 17.sp)
            }
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(moment.username, fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                Spacer(Modifier.height(4.dp))
                Text(moment.content, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(formatMomentTime(moment.createdAt), fontSize = 12.sp, color = TG_TextSecondary)
                    Spacer(Modifier.weight(1f))
                    IconButton(
                        onClick = {
                            val ref = Firebase.firestore.collection("moments").document(moment.id)
                            val newLikes = if (moment.likedByMe) {
                                (listOf(currentUserId ?: ""))
                            } else {
                                (listOf(currentUserId ?: ""))
                            }
                            ref.update("likes", newLikes)
                            onUpdate(moment.copy(likedByMe = !moment.likedByMe, likeCount = if (moment.likedByMe) moment.likeCount - 1 else moment.likeCount + 1))
                        },
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            if (moment.likedByMe) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            null,
                            tint = if (moment.likedByMe) TG_MomentsLike else TG_TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Text("${moment.likeCount}", fontSize = 13.sp, color = TG_TextSecondary)
                }
            }
        }
    }
}

fun formatMomentTime(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp
    return when {
        diff < 60000 -> "刚刚"
        diff < 3600000 -> "${diff / 60000}分钟前"
        diff < 86400000 -> "${diff / 3600000}小时前"
        else -> {
            val cal = java.util.Calendar.getInstance().apply { timeInMillis = timestamp }
            "${cal.get(java.util.Calendar.MONTH) + 1}月${cal.get(java.util.Calendar.DAY_OF_MONTH)}日"
        }
    }
}