package com.github.uragiristereo.mejiboard.presentation.common.mapper

import androidx.compose.runtime.MutableState


inline fun <T> MutableState<T>.update(function: (T) -> T) {
    value = function(value)
}