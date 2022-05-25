package com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.posts.DanbooruPost
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.search.DanbooruSearch
import com.github.uragiristereo.mejiboard.data.model.remote.provider.danbooru.tags.DanbooruTag
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.ImagePost
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagType

fun List<DanbooruPost>.toPostList(): List<Post> {
    val cdnUrl = "https://cdn.donmai.us"

    return this.map {
        val directory: String
        var originalImageUrl = ""
        var scaledImageUrl = ""
        var previewImageUrl = ""

        it.md5?.let { md5 ->
            directory = "${md5.substring(0, 2)}/${md5.substring(2, 4)}"
            originalImageUrl = "$cdnUrl/original/$directory/$md5.${it.fileExt}"
            scaledImageUrl = "$cdnUrl/sample/$directory/sample-$md5.jpg"
            previewImageUrl = "$cdnUrl/preview/$directory/$md5.jpg"
        }

        Post(
            provider = ApiProviders.Danbooru.key,
            id = it.id ?: 0,
            scaled = it.hasLarge ?: false,
            rating = when (it.rating) {
                "g" -> Rating.GENERAL
                "s" -> Rating.SENSITIVE
                "q" -> Rating.QUESTIONABLE
                "e" -> Rating.EXPLICIT
                else -> Rating.GENERAL
            },
            tags = it.tagString,
            uploadedAt = it.createdAt,
            uploader = it.uploaderId.toString(),
            source = it.source,
            originalImage = ImagePost(
                url = originalImageUrl,
                fileType = it.fileExt,
                height = it.imageHeight,
                width = it.imageWidth,
            ),
            scaledImage = ImagePost(
                url = scaledImageUrl,
                fileType = "jpg",
                height = it.imageHeight,
                width = it.imageWidth,
            ),
            previewImage = ImagePost(
                url = previewImageUrl,
                fileType = "jpg",
                height = 180,
                width = ((180f / it.imageHeight) * it.imageWidth).toInt(),
            ),
        )
    }
}

fun List<DanbooruSearch>.toTagList(): List<Tag> {
    val filtered = this.filter { it.type == "tag" }

    return filtered.mapIndexed { index, it ->
        Tag(
            id = index,
            name = it.antecedent ?: it.value,
            count = it.postCount,
            type = when (it.category) {
                0 -> TagType.GENERAL
                1 -> TagType.ARTIST
                3 -> TagType.COPYRIGHT
                4 -> TagType.CHARACTER
                5 -> TagType.METADATA
                else -> TagType.NONE
            },
        )
    }
}

@JvmName("toTagListDanbooruTag")
fun List<DanbooruTag>.toTagList(): List<Tag> {
    return this.mapIndexed { index, it ->
        Tag(
            id = index,
            name = it.name,
            count = it.postCount,
            type = when (it.category) {
                0 -> TagType.GENERAL
                1 -> TagType.ARTIST
                3 -> TagType.COPYRIGHT
                4 -> TagType.CHARACTER
                5 -> TagType.METADATA
                else -> TagType.NONE
            },
        )
    }
}