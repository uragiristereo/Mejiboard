package com.github.uragiristereo.mejiboard.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.util.FileHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    backgroundColor: Color,
) {
    val context = LocalContext.current

    LaunchedEffect(true) {
        launch {
            if (mainViewModel.autoCleanCache)
                FileHelper.autoCleanCache(context, 12)

            delay(300)

            mainNavigation.navigate("main") {
                popUpTo("splash") { inclusive = true }
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(backgroundColor),
        contentAlignment = Alignment.Center
    ) {
        Box(
            Modifier.clip(CircleShape)
        ) {
            Image(
                painterResource(R.drawable.not_like_tsugu),
                "Logo",
                Modifier
                    .size(92.dp)
            )
        }
    }
}