package com.github.uragiristereo.mejiboard.presentation.posts.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun PostsError(
    errorData: String,
    onRetryClick: () -> Unit,
) {
    LazyColumn(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.fillMaxSize(),
    ) {
        item {
            Icon(
                imageVector = Icons.Outlined.Warning,
                contentDescription = null,
                modifier = Modifier.padding(all = 16.dp),
            )
        }

        item {
            Text(
                text = "Error:\n($errorData)",
                textAlign = TextAlign.Center,
            )
        }

        item {
            Button(
                onClick = onRetryClick,
                content = { Text(text = "Retry") },
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}