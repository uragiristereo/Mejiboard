package com.github.uragiristereo.mejiboard.presentation.settings.core

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar

@Composable
fun SettingsTopAppBar(
    state: SettingsState,
    smallHeaderOpacity: Float,
    onBackArrowClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = if (!state.useBigHeader) 4.dp else 0.dp,
        shape = RectangleShape,
    ) {
        TopAppBar(
            backgroundColor = Color.Transparent,
            elevation = 0.dp,
            title = {
                Text(
                    text = "Settings",
                    modifier = Modifier.alpha(alpha = smallHeaderOpacity),
                )
            },
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.statusBars,
                applyBottom = false,
            ),
            navigationIcon = {
                IconButton(
                    onClick = onBackArrowClick,
                    content = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null) }
                )
            },
            modifier = modifier.fillMaxWidth(),
        )
    }
}