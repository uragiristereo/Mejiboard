package com.uragiristereo.mejiboard.util

import android.view.Window
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


fun Window.showSystemBars() {
    WindowInsetsControllerCompat(this, decorView).show(WindowInsetsCompat.Type.systemBars())
}

fun Window.hideSystemBars() {
    WindowInsetsControllerCompat(this, decorView).apply {
        hide(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}