package com.github.uragiristereo.mejiboard.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SplashScreen(
    mainNavigation: NavHostController,
    darkTheme: Boolean
) {
    LaunchedEffect(Unit) {
        launch {
            delay(1000)

            mainNavigation.navigate("main") {
                popUpTo("splash") { inclusive = true }
                launchSingleTop = true
            }
        }
    }
    Box(
        Modifier
            .fillMaxSize()
            .background(if (darkTheme) MaterialTheme.colors.background else MaterialTheme.colors.primaryVariant),
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