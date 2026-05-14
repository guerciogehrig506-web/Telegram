package com.trio.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.app.data.model.Contact
import com.trio.app.data.model.OnlineStatus
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_DarkBackground
import com.trio.app.ui.theme.TG_DarkSurface
import com.trio.app.ui.theme.TG_Online
import com.trio.app.ui.theme.TG_Separator
import com.trio.app.ui.theme.TG_Surface
import com.trio.app.ui.theme.TG_TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ContactDetailSheet(
    contact: Contact,
    onDismiss: () -> Unit,
    onStartChat: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isDark = isSystemInDarkTheme()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isDark) TG_DarkSurface else TG_Surface,
        shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(Modifier.size(80.dp).clip(CircleShape).background(brush = Brush.linearGradient(contact.avatarGradient)), contentAlignment = Alignment.Center) {
                Text(contact.user.username.take(1).uppercase(), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 30.sp)
            }

            Spacer(Modifier.height(12.dp))
            Text(contact.user.username, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
            Spacer(Modifier.height(2.dp))

            val statusText = when (contact.onlineStatus) {
                OnlineStatus.ONLINE -> "在线"
                OnlineStatus.AWAY -> "离开"
                OnlineStatus.OFFLINE -> "离线"
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (contact.onlineStatus == OnlineStatus.ONLINE) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(TG_Online))
                    Spacer(Modifier.width(6.dp))
                }
                Text(statusText, fontSize = 14.sp, color = TG_TextSecondary)
            }

            if (contact.user.department.isNotEmpty()) {
                Spacer(Modifier.height(4.dp))
                Text(contact.user.department, fontSize = 14.sp, color = TG_TextSecondary)
            }

            Spacer(Modifier.height(8.dp))
            Text(contact.user.email, fontSize = 14.sp, color = TG_ActionBar)

            Spacer(Modifier.height(24.dp))

            Row(Modifier.fillMaxWidth().padding(horizontal = 32.dp)) {
                Column(Modifier.weight(1f).clip(RoundedCornerShape(12.dp)).background(TG_ActionBar.copy(alpha = 0.1f)).clickable { onStartChat() }.padding(vertical = 10.dp),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Filled.Chat, null, tint = TG_ActionBar, modifier = Modifier.size(24.dp))
                    Spacer(Modifier.height(4.dp))
                    Text("发消息", fontSize = 13.sp, color = TG_ActionBar, fontWeight = FontWeight.Medium)
                }
            }
        }
    }
}
