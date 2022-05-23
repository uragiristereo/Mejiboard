package com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg

import com.github.uragiristereo.mejiboard.data.model.remote.provider.ApiProviders
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.posts.SafebooruOrgPost
import com.github.uragiristereo.mejiboard.data.model.remote.provider.safebooruorg.search.SafebooruOrgSearch
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.ImagePost
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagType
import java.io.File
import java.util.*

fun List<SafebooruOrgPost>.toPostList(): List<Post> {
    val baseUrl = ApiProviders.SafebooruOrg.baseUrl

    return this.map {
        val fileNameWithoutExtension = it.image
            .substring(
                startIndex = 0,
                endIndex = it.image.lastIndexOf(char = '.'),
            )

        Post(
            type = "safebooruorg",
            id = it.id,
            scaled = it.sample,
            rating = when (it.rating) {
                "safe" -> Rating.SAFE
                "questionable" -> Rating.QUESTIONABLE
                "explicit" -> Rating.EXPLICIT
                else -> Rating.SAFE
            },
            tags = it.tags,
            uploadedAt = Date(it.change * 1000L),
            uploader = it.owner,
            source = "",
            originalImage = ImagePost(
                url = "$baseUrl/images/${it.directory}/${it.image}",
                fileType = File(it.image).extension,
                height = it.height,
                width = it.width,
            ),
            scaledImage = ImagePost(
                url = "$baseUrl/samples/${it.directory}/sample_$fileNameWithoutExtension.jpg",
                fileType = "jpg",
                height = it.sampleHeight,
                width = it.sampleWidth,
            ),
            previewImage = ImagePost(
                url = "$baseUrl/thumbnails/${it.directory}/thumbnail_$fileNameWithoutExtension.jpg",
                fileType = "jpg",
                height = 150,
                width = ((150f / it.height) * it.width).toInt(),
            ),
        )
    }
}

fun List<SafebooruOrgSearch>.toTagList(): List<Tag> {
    return this.mapIndexed { index, it ->
        val count = try {
            it.label
                .replace(oldValue = "${it.value} ", newValue = "")
                .replace(oldValue = "(", newValue = "")
                .replace(oldValue = ")", newValue = "")
                .toInt()
        } catch (_: Throwable) {
            0
        }

        Tag(
            id = index,
            name = it.value,
            count = count,
            type = TagType.NONE,
        )
    }
}