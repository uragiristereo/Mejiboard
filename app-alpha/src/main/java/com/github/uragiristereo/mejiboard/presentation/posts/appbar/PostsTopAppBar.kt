package com.github.uragiristereo.mejiboard.presentation.posts.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.uragiristereo.mejiboard.R

@Composable
fun PostsTopAppBar(
    searchTags: String,
    currentHeight: Dp,
    onHeightChanged: (Dp) -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .onSizeChanged { size ->
                val newHeight = with(density) { size.height.toDp() }

                if (newHeight != currentHeight) {
                    onHeightChanged(newHeight)
                }
            }
            .background(color = MaterialTheme.colors.background),
    ) {
        Card(
            elevation = 2.dp,
            shape = RectangleShape,
        ) {
            TopAppBar(
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                title = { Text(text = "Mejiboard") },
                navigationIcon = {
                    Surface(
                        shape = CircleShape,
                        color = MaterialTheme.colors.onSurface.copy(alpha = 0.2f),
                        modifier = Modifier.padding(8.dp),
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.mejiboard_round),
                            contentDescription = null,
                        )
                    }
                },
                modifier = Modifier.height(56.dp),
            )
        }
        Text(
            text = buildAnnotatedString {
                append("Browse: ")
                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    if (searchTags.isEmpty())
                        append("All posts")
                    else
                        append(searchTags)
                }
            },
            fontSize = 16.sp,
            modifier = Modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp,
                ),
        )
    }
}