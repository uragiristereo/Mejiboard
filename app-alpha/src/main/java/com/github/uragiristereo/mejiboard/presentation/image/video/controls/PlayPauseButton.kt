package com.github.uragiristereo.mejiboard.presentation.image.video.controls

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.google.android.exoplayer2.ExoPlayer

@Composable
fun PlayPauseButton(
    player: ExoPlayer,
    videoPlayed: Boolean,
    onVideoPlayedChange: (Boolean) -> Unit,
    onVolumeSliderVisibleChange: (Boolean) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        IconButton(
            onClick = {
                onVideoPlayedChange(!videoPlayed)
                onVolumeSliderVisibleChange(false)
                player.playWhenReady = !videoPlayed
            },
            modifier = Modifier
                .align(Alignment.Center)
                .size(52.dp)
                .clip(CircleShape)
                .background(Color.Black.copy(0.2f)),
            content = {
                Icon(
                    painter = when {
                        videoPlayed -> painterResource(R.drawable.pause)
                        else -> rememberVectorPainter(Icons.Outlined.PlayArrow)
                    },
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(40.dp)
                )
            }
        )
    }
}