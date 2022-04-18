package com.github.uragiristereo.mejiboard.presentation.splash.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R

@Composable
fun SplashLogo(
    backgroundColor: Color,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center,
    ) {
        Box(modifier = Modifier.clip(CircleShape)) {
            Image(
                painter = painterResource(R.drawable.not_like_tsugu),
                contentDescription = null,
                modifier = Modifier.size(92.dp)
            )
        }
    }
}