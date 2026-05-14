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
import com.trio.app.data.model.Contact
import com.trio.app.data.model.OnlineStatus
import com.trio.app.ui.theme.TG_AwayOrange
import com.trio.app.ui.theme.TG_OfflineGray
import com.trio.app.ui.theme.TG_Online
import com.trio.app.ui.theme.TG_Separator
import com.trio.app.ui.theme.TG_TextLink
import com.trio.app.ui.theme.TG_TextSecondary

@Composable
fun ContactListItem(
    contact: Contact,
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
            Box(modifier = Modifier.size(48.dp)) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(
                            brush = Brush.linearGradient(contact.avatarGradient)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = contact.user.username.take(1).uppercase(),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 19.sp
                    )
                }

                val statusColor = when (contact.onlineStatus) {
                    OnlineStatus.ONLINE -> TG_Online
                    OnlineStatus.AWAY -> TG_AwayOrange
                    OnlineStatus.OFFLINE -> TG_OfflineGray
                }

                Box(modifier = Modifier.size(13.dp).align(Alignment.BottomEnd)) {
                    Box(
                        modifier = Modifier
                            .size(13.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )
                    Box(
                        modifier = Modifier
                            .size(9.dp)
                            .align(Alignment.Center)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = contact.user.username,
                    fontWeight = FontWeight.Medium,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (contact.user.department.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = contact.user.department,
                        fontSize = 13.sp,
                        color = TG_TextSecondary
                    )
                }
            }

            if (contact.user.role == "ADMIN") {
                Text(
                    text = "Admin",
                    fontSize = 12.sp,
                    color = TG_TextLink,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 74.dp)
                .height(0.5.dp)
                .background(TG_Separator)
        )
    }
}
