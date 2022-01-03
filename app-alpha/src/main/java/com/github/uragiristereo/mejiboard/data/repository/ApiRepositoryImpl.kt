package com.github.uragiristereo.mejiboard.data.repository

import com.github.uragiristereo.mejiboard.data.dto.api.PostDto
import com.github.uragiristereo.mejiboard.data.dto.api.SearchDto
import com.github.uragiristereo.mejiboard.data.dto.api.TagDto
import com.github.uragiristereo.mejiboard.domain.repository.ApiRepository
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import okhttp3.ResponseBody
import javax.inject.Inject

class ApiRepositoryImpl @Inject constructor(
    val networkRepository: NetworkRepository,
) : ApiRepository {
    override fun getPosts(pid: Int, tags: String): List<PostDto> {
        TODO("Not yet implemented")
    }

    override fun getTags(term: String): List<SearchDto> {
        TODO("Not yet implemented")
    }

    override fun getTagsInfo(names: String): List<TagDto> {
        TODO("Not yet implemented")
    }

    override fun downloadFile(url: String): ResponseBody {
        TODO("Not yet implemented")
    }

    override fun checkFile(url: String): Void {
        TODO("Not yet implemented")
    }
}