package com.github.uragiristereo.mejiboard.presentation.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun DragHandle() {
    Box(
        modifier = Modifier
            .size(width = 48.dp, height = 4.dp)
            .clip(RoundedCornerShape(percent = 50))
            .background(color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f)),
    )
}
