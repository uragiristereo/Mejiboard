package com.github.uragiristereo.mejiboard

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.core.view.WindowCompat
import coil.annotation.ExperimentalCoilApi
import com.github.uragiristereo.mejiboard.data.repository.PreferencesRepository
import com.github.uragiristereo.mejiboard.presentation.main.MainScreen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@ExperimentalCoilApi
@ExperimentalAnimationApi
@ExperimentalMaterialApi
@ExperimentalComposeUiApi
@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var preferencesRepository: PreferencesRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        if (SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel("downloads", "Downloads", NotificationManager.IMPORTANCE_LOW)
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        setContent {
            MainScreen()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
                preferencesRepository.permissionState.value = "granted"
            else
                preferencesRepository.permissionState.value = "denied"
        } else
            preferencesRepository.permissionState.value = "denied"
    }

    override fun onPause() {
        super.onPause()

        if (preferencesRepository.blockFromRecents.value)
            window.setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE)
    }

    override fun onResume() {
        super.onResume()

        window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
    }
}