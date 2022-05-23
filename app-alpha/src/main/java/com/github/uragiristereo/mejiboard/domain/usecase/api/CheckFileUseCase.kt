package com.github.uragiristereo.mejiboard.domain.usecase.api

import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.domain.repository.remote.NetworkRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay
import okhttp3.Headers
import javax.inject.Inject

class CheckFileUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    suspend operator fun invoke(
        url: String,
        onLoading: (loading: Boolean) -> Unit,
        onSuccess: (data: Headers) -> Unit,
        onFailed: (message: String) -> Unit,
        onError: (t: Throwable) -> Unit,
    ) {
        onLoading(true)

        delay(Constants.API_DELAY)

        try {
            val response = networkRepository.api.checkFile(url)

            if (response.isSuccessful) {
                onSuccess(response.headers())
            } else {
                onFailed(response.raw().body.toString())
            }

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