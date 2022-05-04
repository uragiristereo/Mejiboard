package com.github.uragiristereo.mejiboard.presentation.image.more.route.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp


@Composable
fun SheetItem(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SheetItem(
        content = { Text(text = text) },
        icon = icon,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun SheetItem(
    title: String,
    subtitle: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    SheetItem(
        content = {
            Column {
                Text(text = title)

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f),
                )
            }
        },
        icon = icon,
        onClick = onClick,
        modifier = modifier,
    )
}

@Composable
fun SheetItem(
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Box(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 24.dp,
                    bottom = 16.dp,
                )
                .size(24.dp),
            content = { icon() },
        )

        Box(
            modifier = Modifier
                .padding(
                    top = 8.dp,
                    bottom = 8.dp,
                ),
            content = { content() },
        )
    }
}