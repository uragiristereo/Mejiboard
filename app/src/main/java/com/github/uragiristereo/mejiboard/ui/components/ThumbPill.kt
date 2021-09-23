package com.github.uragiristereo.mejiboard.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun ThumbPill() {
    Surface(
        Modifier
            .width(48.dp)
            .height(4.dp),
        color = MaterialTheme.colors.onSurface.copy(0.2f),
        shape = RoundedCornerShape(50)
    ) { }
}

@Preview
@Composable
fun P() {
    Box(
        Modifier
            .fillMaxWidth()
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        ThumbPill()
    }
}