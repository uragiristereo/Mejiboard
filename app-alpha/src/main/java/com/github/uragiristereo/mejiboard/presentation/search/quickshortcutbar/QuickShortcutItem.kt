package com.github.uragiristereo.mejiboard.presentation.search.quickshortcutbar

import androidx.compose.foundation.layout.*
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun QuickShortcutItem(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(modifier = modifier.height(36.dp)) {
        TextButton(
            onClick = onClick,
            modifier = Modifier
                .fillMaxHeight()
                .widthIn(min = 48.dp),
            content = { Text(text = text) },
        )

        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
        )
    }
}