package com.github.uragiristereo.mejiboard.presentation.settings.core

import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun BigHeader(
    bigHeaderOpacity: Float,
    onSizeChange: (Int) -> Unit,
) {
    Text(
        text = "Settings",
        style = MaterialTheme.typography.h4,
        fontSize = 36.sp,
        modifier = Modifier
            .alpha(alpha = bigHeaderOpacity)
            .onGloballyPositioned {
                onSizeChange(it.size.height)
            }
            .padding(
                top = 48.dp,
                bottom = 12.dp,
                start = 16.dp,
            ),
    )
}