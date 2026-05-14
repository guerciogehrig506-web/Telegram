package com.trio.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Contacts
import androidx.compose.material.icons.filled.DynamicFeed
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.Chat
import androidx.compose.material.icons.outlined.Contacts
import androidx.compose.material.icons.outlined.DynamicFeed
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.trio.app.ui.theme.TG_ActionBar
import com.trio.app.ui.theme.TG_DarkBackground
import com.trio.app.ui.theme.TG_DarkSurface

data class BottomNavItem(
    val route: String,
    val label: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

val bottomNavItems = listOf(
    BottomNavItem("chats", "消息", Icons.Filled.Chat, Icons.Outlined.Chat),
    BottomNavItem("contacts", "通讯录", Icons.Filled.Contacts, Icons.Outlined.Contacts),
    BottomNavItem("moments", "动态", Icons.Filled.DynamicFeed, Icons.Outlined.DynamicFeed),
    BottomNavItem("profile", "我的", Icons.Filled.Person, Icons.Outlined.Person)
)

@Composable
fun LiquidGlassBottomBar(currentRoute: String?, onItemClick: (String) -> Unit) {
    val isDark = isSystemInDarkTheme()
    val glassGradient = if (isDark)
        listOf(Color(0xD91C1C1E), Color(0xE61C1C1E))
    else
        listOf(Color.White.copy(alpha = 0.75f), Color.White.copy(alpha = 0.85f))

    var barVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { barVisible = true }

    AnimatedVisibility(
        visible = barVisible,
        enter = fadeIn(tween(400)) + slideInVertically(tween(400)) { it / 2 }
    ) {
        Box(Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp), contentAlignment = Alignment.Center) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(28.dp))
                    .background(brush = Brush.verticalGradient(glassGradient))
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                bottomNavItems.forEach { item ->
                    val isSelected = currentRoute == item.route
                    val iconScale by animateFloatAsState(if (isSelected) 1.15f else 1f, spring(0.55f, 400f), label = "s")
                    val iconColor by animateColorAsState(if (isSelected) TG_ActionBar else Color.Gray.copy(alpha = 0.6f), spring(0.6f), label = "c")

                    Column(
                        Modifier.weight(1f).clickable(remember { MutableInteractionSource() }, null) { onItemClick(item.route) },
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        if (isSelected) {
                            Box(Modifier.size(40.dp).clip(RoundedCornerShape(14.dp)).background(TG_ActionBar.copy(alpha = 0.12f)), contentAlignment = Alignment.Center) {
                                Icon(item.selectedIcon, item.label, tint = iconColor, modifier = Modifier.size(22.dp).scale(iconScale))
                            }
                        } else {
                            Icon(item.unselectedIcon, item.label, tint = iconColor, modifier = Modifier.size(22.dp).scale(iconScale))
                        }
                        Spacer(Modifier.height(2.dp))
                        Text(item.label, fontSize = 10.sp, color = iconColor, maxLines = 1)
                    }
                }
            }
        }
    }
}
