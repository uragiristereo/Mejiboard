package com.github.uragiristereo.mejiboard.presentation.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
    modifier: Modifier = Modifier
) {
    SheetItem(
        content = { Text(text) },
        icon = icon,
        onClick = onClick,
        modifier = modifier
    )
}

@Composable
fun SheetItem(
    content: @Composable () -> Unit,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable(
                onClick = onClick
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .padding(
                    start = 16.dp,
                    top = 16.dp,
                    end = 24.dp,
                    bottom = 16.dp
                )
                .size(24.dp)
        ) {
            icon()
        }
        Box(
            Modifier
                .padding(
                    top = 8.dp,
                    bottom = 8.dp
                )
        ) {
            content()
        }
    }
}