package com.github.uragiristereo.mejiboard.common

object Constants {
    const val BASE_URL = "https://gelbooru.com/"
    const val STATE_KEY_SELECTED_POST = "main.selected.post"
    const val STATE_KEY_NOTIFICATION_COUNT = "main.notification.count"
    const val STATE_KEY_SEARCH_TAGS = "main.search.tags"
    const val STATE_KEY_POSTS = "posts.state"
    const val API_DELAY = 400L
    const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:95.0) Gecko/20100101 Firefox/95.0"
    const val KEY_LOAD_MORE_PROGRESS = "posts.post.load.more"

    val SUPPORTED_TYPES_IMAGE = listOf("jpg", "jpeg", "png", "gif")
    val SUPPORTED_TYPES_ANIMATION = listOf("webm", "mp4")
}