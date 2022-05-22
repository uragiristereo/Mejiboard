package com.github.uragiristereo.mejiboard.common.helper

import com.github.uragiristereo.mejiboard.domain.entity.provider.post.ImagePost

object ImageHelper {
    fun resizeImage(postImage: ImagePost): Pair<Int, Int> {
        val maxSize = 4096f

        return if (postImage.width > maxSize || postImage.height > maxSize) {
            val scale =
                if (postImage.width > postImage.height)
                    maxSize.div(postImage.width)
                else
                    maxSize.div(postImage.height)

            val scaledWidth = postImage.width * scale
            val scaledHeight = postImage.height * scale

            Pair(scaledWidth.toInt(), scaledHeight.toInt())
        } else
            Pair(postImage.width, postImage.height)
    }
}