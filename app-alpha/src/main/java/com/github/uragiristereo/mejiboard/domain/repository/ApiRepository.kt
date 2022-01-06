package com.github.uragiristereo.mejiboard.domain.repository

import com.github.uragiristereo.mejiboard.data.dto.api.PostDto
import com.github.uragiristereo.mejiboard.data.dto.api.SearchDto
import com.github.uragiristereo.mejiboard.data.dto.api.tag.TagDto
import okhttp3.ResponseBody

interface ApiRepository {
    fun getPosts(
        pid: Int,
        tags: String,
    ): List<PostDto>

    fun getTags(term: String): List<SearchDto>

    fun getTagsInfo(names: String): List<TagDto>

    fun downloadFile(url: String): ResponseBody

    fun checkFile(url: String): Void
}