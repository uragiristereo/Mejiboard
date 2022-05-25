package com.github.uragiristereo.mejiboard.common

object Constants {
    const val STATE_KEY_NOTIFICATION_COUNT = "main.notification.count"
    const val STATE_KEY_POSTS = "posts.state"
    const val API_DELAY = 200L
    const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0"
    const val KEY_LOAD_MORE_PROGRESS = "posts.post.load.more"
    const val SAFE_LISTING_ONLY_MODE = false

    val SUPPORTED_TYPES_IMAGE = listOf("jpg", "jpeg", "png", "gif")
    val SUPPORTED_TYPES_VIDEO = listOf("webm", "mp4")
    val SUPPORTED_TYPES_ANIMATED = SUPPORTED_TYPES_VIDEO + listOf("gif")
}