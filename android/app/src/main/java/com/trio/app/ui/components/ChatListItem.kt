package com.trio.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DoneAll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.app.data.model.ChatPreview
import com.trio.app.data.model.ReadStatus
import com.trio.app.ui.theme.TG_CheckDelivered
import com.trio.app.ui.theme.TG_CheckRead
import com.trio.app.ui.theme.TG_CheckSent
import com.trio.app.ui.theme.TG_Online
import com.trio.app.ui.theme.TG_Separator
import com.trio.app.ui.theme.TG_TextSecondary
import com.trio.app.ui.theme.TG_UnreadBadge

@Composable
fun ChatListItem(
    chat: ChatPreview,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 9.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(modifier = Modifier.size(54.dp)) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(chat.contact.avatarGradient)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = chat.contact.user.username.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp
                    )
                }

                if (chat.contact.onlineStatus == com.trio.app.data.model.OnlineStatus.ONLINE) {
                    Box(modifier = Modifier.size(14.dp).align(Alignment.BottomEnd)) {
                        Box(
                            modifier = Modifier
                                .size(14.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                        )
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .align(Alignment.Center)
                                .clip(CircleShape)
                                .background(TG_Online)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.contact.user.username,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = chat.lastMessageTime,
                        fontSize = 13.sp,
                        color = TG_TextSecondary
                    )
                }

                Spacer(modifier = Modifier.height(3.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = chat.lastMessage,
                        fontSize = 14.sp,
                        color = TG_TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    ReadStatusIcon(status = chat.readStatus)

                    if (chat.unreadCount > 0) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Box(
                            modifier = Modifier
                                .height(20.dp)
                                .width(if (chat.unreadCount > 99) 28.dp else 20.dp)
                                .clip(CircleShape)
                                .background(TG_UnreadBadge),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = if (chat.unreadCount > 99) "99+" else "${chat.unreadCount}",
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 80.dp)
                .height(0.5.dp)
                .background(TG_Separator)
        )
    }
}

@Composable
private fun ReadStatusIcon(status: ReadStatus) {
    when (status) {
        ReadStatus.SENDING -> {}
        ReadStatus.SENT -> {
            Icon(
                imageVector = Icons.Filled.Check,
                contentDescription = null,
                tint = TG_CheckSent,
                modifier = Modifier.size(16.dp)
            )
        }
        ReadStatus.DELIVERED -> {
            Icon(
                imageVector = Icons.Filled.DoneAll,
                contentDescription = null,
                tint = TG_CheckDelivered,
                modifier = Modifier.size(16.dp)
            )
        }
        ReadStatus.READ -> {
            Icon(
                imageVector = Icons.Filled.DoneAll,
                contentDescription = null,
                tint = TG_CheckRead,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}
