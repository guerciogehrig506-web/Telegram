package com.trio.app.ui.animation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.SpringSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

object TrioAnimation {
    val defaultDuration = 350
    val fastDuration = 200
    val slowDuration = 500
    val springBounce: SpringSpec<Float> = spring(dampingRatio = Spring.DampingRatioMediumBouncy, stiffness = Spring.StiffnessLow)
    val springSnap: SpringSpec<Float> = spring(dampingRatio = Spring.DampingRatioNoBouncy, stiffness = Spring.StiffnessMedium)
    val tweenEase = tween<Float>(defaultDuration, easing = FastOutSlowInEasing)
    val tweenFast = tween<Float>(fastDuration, easing = FastOutSlowInEasing)

    val enterFadeScale = fadeIn(tween(defaultDuration)) + scaleIn(tween(defaultDuration), initialScale = 0.92f)
    val exitFadeScale = fadeOut(tween(fastDuration)) + scaleOut(tween(fastDuration), targetScale = 0.92f)

    val enterFromBottom = slideInVertically(tween(defaultDuration)) { it / 3 } + fadeIn(tween(defaultDuration))
    val exitToBottom = slideOutVertically(tween(defaultDuration)) { it / 3 } + fadeOut(tween(fastDuration))

    val listItemEnter = slideInVertically(tween(defaultDuration)) { it / 6 } + fadeIn(tween(defaultDuration))
    val listItemExit = fadeOut(tween(fastDuration))

    val messageEnter = slideInVertically(tween(300)) { it / 5 } + fadeIn(tween(250))
}

fun Modifier.pressScale(): Modifier = composed {
    var pressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (pressed) 0.96f else 1f, TrioAnimation.springSnap, label = "press")
    this
        .scale(scale)
        .then(
            Modifier.composed {
                LaunchedEffect(pressed) {
                    if (pressed) {
                        delay(80)
                        pressed = false
                    }
                }
                this
            }
        )
}

@Composable
fun AnimatedListItem(
    index: Int,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    var visible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 60L)
        visible = true
    }
    AnimatedVisibility(
        visible = visible,
        enter = TrioAnimation.listItemEnter,
        exit = TrioAnimation.listItemExit,
        modifier = modifier
    ) {
        content()
    }
}

@Composable
fun AnimatedContentTransition(
    visible: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        enter = TrioAnimation.enterFadeScale,
        exit = TrioAnimation.exitFadeScale,
        modifier = modifier
    ) {
        content()
    }
}