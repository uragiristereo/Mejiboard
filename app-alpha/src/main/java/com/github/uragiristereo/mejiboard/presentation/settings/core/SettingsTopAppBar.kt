package com.github.uragiristereo.mejiboard.presentation.settings.core

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.unit.dp

@Composable
fun SettingsTopAppBar(
    useBigHeader: Boolean,
    smallHeaderOpacity: Float,
    onNavigateUp: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        elevation = if (!useBigHeader) 4.dp else 0.dp,
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
//            contentPadding = rememberInsetsPaddingValues(
//                insets = LocalWindowInsets.current.statusBars,
//                applyBottom = false,
//            ),
            navigationIcon = {
                IconButton(
                    onClick = onNavigateUp,
                    content = {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = null,
                        )
                    },
                )
            },
            modifier = modifier
                .fillMaxWidth()
                .padding(
                    paddingValues = WindowInsets.statusBars
                        .only(WindowInsetsSides.Horizontal + WindowInsetsSides.Top)
                        .asPaddingValues()
                ),
        )
    }
}