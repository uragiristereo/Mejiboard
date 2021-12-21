package com.github.uragiristereo.mejiboard.util

import com.github.uragiristereo.mejiboard.model.network.Post
import java.io.File

object ImageHelper {
    fun resizeImage(post: Post): Pair<Int, Int> {
        val maxSize = 4096f

        return if (post.width > maxSize || post.height > maxSize) {
            val scale =
                if (post.width > post.height)
                    maxSize.div(post.width)
                else
                    maxSize.div(post.height)

            val scaledWidth = post.width * scale
            val scaledHeight = post.height * scale

            Pair(scaledWidth.toInt(), scaledHeight.toInt())
        } else
            Pair(post.width, post.height)
    }

    fun parseImageUrl(
        post: Post,
        original: Boolean,
    ): String {
        val imageType = File(post.image).extension
        val originalImageUrl = "https://img3.gelbooru.com/images/${post.directory}/${post.hash}.$imageType"

        return if (original)
            originalImageUrl
        else
            if (post.sample == 1 && imageType != "gif")
                "https://img3.gelbooru.com/samples/${post.directory}/sample_${post.hash}.jpg"
            else
                originalImageUrl
    }
}