package com.github.uragiristereo.mejiboard.domain.usecase

import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import okhttp3.Headers
import javax.inject.Inject

@Suppress("RemoveExplicitTypeArguments")
class CheckFileUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    operator fun invoke(url: String): Flow<Resource<Headers>> {
        return flow {
            emit(Resource.Loading())

            val response = networkRepository.api.checkFile(url)
            val headers = response.headers()

            emit(Resource.Success(headers))
        }
    }
}