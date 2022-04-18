package com.github.uragiristereo.mejiboard.presentation.posts.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import soup.compose.material.motion.MaterialFade
import soup.compose.material.motion.MotionConstants

@ExperimentalAnimationApi
@Composable
fun PostsFab(
    visible: Boolean,
    gridState: LazyListState,
) {
    val scope = rememberCoroutineScope()

    MaterialFade(
        visible = visible,
        exitDurationMillis = MotionConstants.motionDurationShort2,
    ) {
        FloatingActionButton(
            onClick = { scope.launch { gridState.animateScrollToItem(0) } },
            content = { Icon(imageVector = Icons.Outlined.KeyboardArrowUp, contentDescription = null) },
        )
    }
}