package com.github.uragiristereo.mejiboard.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.ui.navigation.MainNavigation
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.plant(Timber.DebugTree())
        WindowCompat.setDecorFitsSystemWindows(window, false)

        val channel = NotificationChannel("downloads", "Downloads", NotificationManager.IMPORTANCE_LOW)
        val manager = getSystemService(NotificationManager::class.java)
        manager.createNotificationChannel(channel)

        setContent {
            MainNavigation()
        }
    }
}