package com.github.uragiristereo.mejiboard.domain.usecase.api

import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.common.mapper.api.toTag
import com.github.uragiristereo.mejiboard.domain.entity.Tag
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import javax.inject.Inject

class GetTagsInfoUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    suspend operator fun invoke(
        names: String,
        onLoading: (loading: Boolean) -> Unit,
        onSuccess: (data: List<Tag>) -> Unit,
        onFailed: (message: String) -> Unit,
        onError: (t: Throwable) -> Unit,
    ) {
        onLoading(true)

        delay(Constants.API_DELAY)

        try {
            val response = networkRepository.api.getTagsInfo(names)

            if (response.isSuccessful)
                response.body()?.let { data ->
                    onSuccess(data.tag?.toTag() ?: emptyList())
                }
            else
                onFailed(response.raw().body.toString())

            onLoading(false)
        } catch (t: Throwable) {
            when (t) {
                is CancellationException -> {}
                else -> {
                    onLoading(false)
                    onError(t)
                }
            }
        }
    }
}