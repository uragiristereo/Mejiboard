package com.github.uragiristereo.mejiboard.domain.usecase

import com.github.uragiristereo.mejiboard.common.extension.toSearch
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Search
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Suppress("RemoveExplicitTypeArguments")
class GetTagsUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    operator fun invoke(term: String): Flow<Resource<List<Search>>> {
        return flow {
            emit(Resource.Loading())

            val response = networkRepository.api.getTags(term)

            if (response.isSuccessful)
                response.body()?.let { result ->
                    val searches = result.map { it.toSearch() }
                    emit(Resource.Success(searches))
                }
            else
                emit(Resource.Error<List<Search>>(response.errorBody().toString()))
        }
    }
}