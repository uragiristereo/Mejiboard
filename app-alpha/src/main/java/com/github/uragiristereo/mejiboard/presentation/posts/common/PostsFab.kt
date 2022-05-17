package com.github.uragiristereo.mejiboard.presentation.posts.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable
import soup.compose.material.motion.MaterialFade
import soup.compose.material.motion.MotionConstants

@ExperimentalAnimationApi
@Composable
fun PostsFab(
    visible: Boolean,
    onClick: () -> Unit,
) {
    MaterialFade(
        visible = visible,
        exitDurationMillis = MotionConstants.motionDurationShort2,
    ) {
        FloatingActionButton(
            onClick = onClick,
            content = {
                Icon(
                    imageVector = Icons.Outlined.KeyboardArrowUp,
                    contentDescription = null,
                )
            },
        )
    }
}