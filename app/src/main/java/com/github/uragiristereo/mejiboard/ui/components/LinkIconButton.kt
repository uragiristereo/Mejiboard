package com.github.uragiristereo.mejiboard.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LinkIconButton(
    icon: ImageVector,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .clickable {
                onClick()
            }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            text,
            Modifier.size(16.dp)
        )
        Text(
            text.uppercase(),
            Modifier.padding(start = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun LinkIconButton(
    painter: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier
            .clickable {
                onClick()
            }
            .padding(4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter,
            text,
            Modifier.size(16.dp)
        )
        Text(
            text.uppercase(),
            Modifier.padding(start = 4.dp),
            fontWeight = FontWeight.Bold
        )
    }
}