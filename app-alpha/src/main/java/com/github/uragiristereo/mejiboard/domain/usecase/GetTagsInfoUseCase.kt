package com.github.uragiristereo.mejiboard.domain.usecase

import com.github.uragiristereo.mejiboard.common.extension.toTag
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Tag
import com.github.uragiristereo.mejiboard.domain.repository.NetworkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

@Suppress("RemoveExplicitTypeArguments")
class GetTagsInfoUseCase @Inject constructor(
    private val networkRepository: NetworkRepository,
) {
    operator fun invoke(names: String): Flow<Resource<List<Tag>>> {
        return flow {
            emit(Resource.Loading())

            val response = networkRepository.api.getTagsInfo(names)

            if (response.isSuccessful)
                response.body()?.let { result ->
                    val tags = result.map { it.toTag() }
                    emit(Resource.Success(tags))
                }
            else
                emit(Resource.Error<List<Tag>>(response.errorBody().toString()))
        }
    }
}