package com.github.uragiristereo.mejiboard.common.extension

import android.view.Window
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat


fun Window.showSystemBars() {
    ViewCompat.getWindowInsetsController(decorView)?.show(WindowInsetsCompat.Type.systemBars())
}

fun Window.hideSystemBars() {
    ViewCompat.getWindowInsetsController(decorView)?.apply {
        hide(WindowInsetsCompat.Type.systemBars())
        systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
    }
}