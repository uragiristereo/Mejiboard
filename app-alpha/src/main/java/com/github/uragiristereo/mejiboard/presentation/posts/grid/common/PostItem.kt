package com.github.uragiristereo.mejiboard.presentation.posts.grid.common

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post

@Composable
fun PostItem(
    item: Post,
    allowPostClick: Boolean,
    onNavigateImage: (Post) -> Unit,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val aspectRatio = remember {
        val ratio = item.originalImage.width.toFloat() / item.originalImage.height

        ratio.coerceIn(
            minimumValue = 0.5f,
            maximumValue = 2f,
        )
    }

    val borderColor = remember {
        when (item.originalImage.fileType) {
            "gif" -> Color.Cyan
            "webm" -> Color.Blue
            "mp4" -> Color.Blue
            else -> Color.Transparent
        }
    }

    Box(
        modifier = modifier
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(size = 4.dp))
            .border(
                BorderStroke(
                    width = if (item.originalImage.fileType in Constants.SUPPORTED_TYPES_ANIMATED) 4.dp else 0.dp,
                    color = borderColor,
                )
            )
            .clickable {
                if (allowPostClick) {
                    onNavigateImage(item)
                }
            }
    ) {
        PostPlaceholderLoading()

        SubcomposeAsyncImage(
            model = remember {
                ImageRequest.Builder(context)
                    .data(item.previewImage.url)
                    .crossfade(durationMillis = 170)
                    .size(width = item.previewImage.width, height = item.previewImage.height)
                    .build()
            },
            contentDescription = null,
            contentScale = ContentScale.Crop,
            loading = {
                PostPlaceholderLoading()
            },
            error = {
                PostPlaceholderLoading()
            },
            modifier = Modifier.fillMaxSize(),
        )
    }
}