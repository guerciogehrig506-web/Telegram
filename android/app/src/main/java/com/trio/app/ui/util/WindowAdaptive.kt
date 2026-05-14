package com.trio.app.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

enum class WindowSizeClass { COMPACT, MEDIUM, EXPANDED }

data class AdaptiveDimens(
    val horizontalPadding: Dp,
    val contentMaxWidth: Dp,
    val avatarSize: Dp,
    val listAvatarSize: Dp,
    val chatBubbleMaxWidth: Dp,
    val bottomBarPadding: Dp
) {
    companion object {
        @Composable
        fun forSize(sizeClass: WindowSizeClass): AdaptiveDimens = when (sizeClass) {
            WindowSizeClass.COMPACT -> AdaptiveDimens(
                horizontalPadding = 16.dp,
                contentMaxWidth = Dp.Unspecified,
                avatarSize = 44.dp,
                listAvatarSize = 52.dp,
                chatBubbleMaxWidth = 280.dp,
                bottomBarPadding = 16.dp
            )
            WindowSizeClass.MEDIUM -> AdaptiveDimens(
                horizontalPadding = 32.dp,
                contentMaxWidth = 600.dp,
                avatarSize = 56.dp,
                listAvatarSize = 64.dp,
                chatBubbleMaxWidth = 400.dp,
                bottomBarPadding = 64.dp
            )
            WindowSizeClass.EXPANDED -> AdaptiveDimens(
                horizontalPadding = 48.dp,
                contentMaxWidth = 800.dp,
                avatarSize = 72.dp,
                listAvatarSize = 80.dp,
                chatBubbleMaxWidth = 500.dp,
                bottomBarPadding = 128.dp
            )
        }
    }
}

@Composable
fun rememberAdaptiveDimens(widthDp: Float): AdaptiveDimens {
    val sizeClass = when {
        widthDp < 600f -> WindowSizeClass.COMPACT
        widthDp < 840f -> WindowSizeClass.MEDIUM
        else -> WindowSizeClass.EXPANDED
    }
    return AdaptiveDimens.forSize(sizeClass)
}