package com.github.uragiristereo.mejiboard.presentation.posts.common

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.KeyboardArrowUp
import androidx.compose.runtime.Composable

@ExperimentalAnimationApi
@Composable
fun PostsFab(
    visible: Boolean,
    onClick: () -> Unit,
) {
    AnimatedVisibility(
        visible = visible,
        enter = scaleIn(),
        exit = scaleOut(),
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