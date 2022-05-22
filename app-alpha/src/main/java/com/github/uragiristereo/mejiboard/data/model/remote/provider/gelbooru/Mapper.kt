package com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru

import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.posts.GelbooruPostsResult
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.search.GelbooruSearch
import com.github.uragiristereo.mejiboard.data.model.remote.provider.gelbooru.tags.GelbooruTagsResult
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.ImagePost
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagType
import java.io.File

fun GelbooruPostsResult.toPostList(): List<Post> {
    val domain = ApiProviders.Gelbooru.domain

    return this.post?.map {
        val fileType = File(it.image).extension

        Post(
            id = it.id,
            scaled = it.sample == 1,
            rating = when (it.rating) {
                "safe" -> Rating.SAFE
                "questionable" -> Rating.QUESTIONABLE
                "explicit" -> Rating.EXPLICIT
                else -> Rating.SAFE
            },
            tags = it.tags,
            uploadedAt = it.createdAt,
            uploader = it.owner,
            source = try {
                (it.source) as String
            } catch (t: Throwable) {
                ""
            },
            originalImage = ImagePost(
                url = when (fileType) {
                    in Constants.SUPPORTED_TYPES_VIDEO -> "https://video-cdn3.$domain/images/${it.directory}/${it.md5}.$fileType"
                    else -> "https://img3.$domain/images/${it.directory}/${it.md5}.$fileType"
                },
                fileType = fileType,
                height = it.height,
                width = it.width,
            ),
            scaledImage = ImagePost(
                url = "https://img3.$domain/samples/${it.directory}/sample_${it.md5}.jpg",
                fileType = "jpg",
                height = it.sampleHeight,
                width = it.sampleWidth,
            ),
            previewImage = ImagePost(
                url = "https://img3.$domain/thumbnails/${it.directory}/thumbnail_${it.md5}.jpg",
                fileType = "jpg",
                height = it.previewHeight,
                width = it.previewWidth,
            ),
        )
    }
        ?: emptyList()
}

fun List<GelbooruSearch>.toTagList(): List<Tag> {
    return this.mapIndexed { index, it ->
        Tag(
            id = index,
            name = it.value,
            count = it.postCount,
            type = when (it.type) {
                "tag" -> TagType.GENERAL
                "artist" -> TagType.ARTIST
                "copyright" -> TagType.COPYRIGHT
                "character" -> TagType.CHARACTER
                "metadata" -> TagType.METADATA
                else -> TagType.NONE
            },
        )
    }
}

fun GelbooruTagsResult.toTagList(): List<Tag> {
    return this.tag?.map {
        Tag(
            id = it.id,
            name = it.name,
            count = it.count,
            type = when (it.type) {
                0 -> TagType.GENERAL
                1 -> TagType.ARTIST
                3 -> TagType.COPYRIGHT
                4 -> TagType.CHARACTER
                5 -> TagType.METADATA
                else -> TagType.NONE
            },
        )
    }
        ?: emptyList()
}