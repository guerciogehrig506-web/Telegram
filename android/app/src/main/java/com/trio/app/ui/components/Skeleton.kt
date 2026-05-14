package com.trio.app.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun SkeletonBox(
    modifier: Modifier = Modifier,
    width: Dp = 0.dp,
    height: Dp = 16.dp,
    radius: Dp = 4.dp
) {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val shimmerColor = if (isDark) Color(0xFF3A3A3C) else Color(0xFFF2F2F7)

    val transition = rememberInfiniteTransition(label = "skeleton")
    val translateX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, shimmerColor, baseColor),
        start = Offset(translateX - 200f, 0f),
        end = Offset(translateX, 0f)
    )

    Box(
        modifier
            .let { if (width > 0.dp) it.width(width) else it.fillMaxWidth() }
            .height(height)
            .clip(RoundedCornerShape(radius))
            .background(brush)
    )
}

@Composable
fun SkeletonCircle(size: Dp = 48.dp) {
    val isDark = isSystemInDarkTheme()
    val baseColor = if (isDark) Color(0xFF2C2C2E) else Color(0xFFE5E5EA)
    val shimmerColor = if (isDark) Color(0xFF3A3A3C) else Color(0xFFF2F2F7)

    val transition = rememberInfiniteTransition(label = "skeleton_circle")
    val translateX by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "skeleton_circle_translate"
    )

    val brush = Brush.linearGradient(
        colors = listOf(baseColor, shimmerColor, baseColor),
        start = Offset(translateX - 200f, 0f),
        end = Offset(translateX, 0f)
    )

    Box(
        Modifier.size(size).clip(CircleShape).background(brush)
    )
}

@Composable
fun ChatSkeletonItem() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 8.dp)
    ) {
        SkeletonCircle(52.dp)
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f).padding(vertical = 4.dp)) {
            SkeletonBox(width = 120.dp, height = 14.dp, radius = 6.dp)
            Spacer(Modifier.height(8.dp))
            SkeletonBox(width = 200.dp, height = 12.dp, radius = 6.dp)
            Spacer(Modifier.height(8.dp))
            SkeletonBox(width = 100.dp, height = 10.dp, radius = 5.dp)
        }
    }
}

@Composable
fun ContactSkeletonItem() {
    Row(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        SkeletonCircle(48.dp)
        Spacer(Modifier.width(14.dp))
        Column(Modifier.weight(1f).padding(vertical = 6.dp)) {
            SkeletonBox(width = 140.dp, height = 14.dp, radius = 6.dp)
            Spacer(Modifier.height(8.dp))
            SkeletonBox(width = 80.dp, height = 12.dp, radius = 6.dp)
        }
    }
}

@Composable
fun MomentSkeletonItem() {
    Column(
        Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
            SkeletonCircle(40.dp)
            Spacer(Modifier.width(10.dp))
            Column {
                SkeletonBox(width = 100.dp, height = 14.dp, radius = 6.dp)
                Spacer(Modifier.height(4.dp))
                SkeletonBox(width = 60.dp, height = 10.dp, radius = 5.dp)
            }
        }
        Spacer(Modifier.height(10.dp))
        SkeletonBox(height = 14.dp, radius = 6.dp)
        Spacer(Modifier.height(6.dp))
        SkeletonBox(width = 220.dp, height = 14.dp, radius = 6.dp)
    }
}