package com.github.uragiristereo.mejiboard.presentation.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun LinkIconButton(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .padding(all = 4.dp),
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(size = 16.dp),
        )

        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}

@Composable
fun LinkIconButton(
    text: String,
    painter: Painter,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .padding(all = 4.dp),
    ) {
        Icon(
            painter = painter,
            contentDescription = null,
            tint = Color.Unspecified,
            modifier = Modifier.size(size = 16.dp),
        )

        Text(
            text = text.uppercase(),
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(start = 4.dp),
        )
    }
}