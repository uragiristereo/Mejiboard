package com.github.uragiristereo.mejiboard.presentation.posts.appbar

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.uragiristereo.mejiboard.R
import kotlin.math.roundToInt

@Composable
fun PostsTopAppBar(
    toolbarOffsetHeightPx: Float,
    animatedToolbarOffsetHeightPx: Float,
    animationInProgress: Boolean,
    onBrowseHeightChange: (Float) -> Unit,
    searchTags: String,
) {
    Column(
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y =
                    if (animationInProgress)
                        animatedToolbarOffsetHeightPx.roundToInt()
                    else
                        toolbarOffsetHeightPx.roundToInt(),
                )
            }
            .background(color = MaterialTheme.colors.background),
    ) {
        Card(
            elevation = 4.dp,
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
                            painter = painterResource(id = R.drawable.not_like_tsugu),
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
                .onGloballyPositioned { onBrowseHeightChange(it.size.height.toFloat()) }
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp,
                ),
        )
    }
}