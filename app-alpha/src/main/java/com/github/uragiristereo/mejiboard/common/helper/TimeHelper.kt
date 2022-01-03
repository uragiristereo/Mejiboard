package com.github.uragiristereo.mejiboard.common.helper

object TimeHelper {
    fun formatMillis(millis: Long): String {
        val minutes = (millis / 1000 / 60).toString().padStart(2, '0')
        val seconds = (millis / 1000 % 60).toString().padStart(2, '0')

        return "$minutes:$seconds"
    }
}