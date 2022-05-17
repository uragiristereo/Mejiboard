package com.github.uragiristereo.mejiboard.presentation.splash

import android.widget.Toast
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.common.helper.FileHelper
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.splash.common.SplashLogo
import com.github.uragiristereo.mejiboard.presentation.splash.common.SplashUpdate
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@Composable
fun SplashScreen(
    mainViewModel: MainViewModel,
    backgroundColor: Color,
) {
    val context = LocalContext.current
    val systemUiController = rememberSystemUiController()
    val preferences = mainViewModel.preferences

    LaunchedEffect(Unit) {
        systemUiController.setSystemBarsColor(Color.Transparent)

        if (BuildConfig.DEBUG)
            mainViewModel.updateStatus = "latest"
        else
            mainViewModel.checkForUpdate()

        if (preferences.autoCleanCache)
            FileHelper.autoCleanCache(
                context = context,
                hoursDiff = 12,
            )
    }

    DisposableEffect(mainViewModel.updateStatus) {
        if (mainViewModel.updateStatus == "update_available" || mainViewModel.updateStatus == "latest" || mainViewModel.updateStatus == "failed") {
            if (mainViewModel.updateStatus == "failed")
                Toast.makeText(context, "Failed to check for update,\nplease check your internet connection", Toast.LENGTH_LONG).show()

            mainViewModel.splashShown = true
        }

        onDispose { }
    }

    Box {
        SplashLogo(backgroundColor = backgroundColor)

        SplashUpdate(
            mainViewModel = mainViewModel,
            modifier = Modifier.align(Alignment.BottomCenter),
        )
    }
}