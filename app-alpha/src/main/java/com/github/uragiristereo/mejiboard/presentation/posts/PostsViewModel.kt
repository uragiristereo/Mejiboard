package com.github.uragiristereo.mejiboard.presentation.posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.common.extension.toPost
import com.github.uragiristereo.mejiboard.common.extension.toSessionPost
import com.github.uragiristereo.mejiboard.data.database.AppDatabase
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.domain.usecase.GetPostsUseCase
import com.github.uragiristereo.mejiboard.presentation.posts.core.PostsSavedState
import com.github.uragiristereo.mejiboard.presentation.posts.core.PostsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getPostsUseCase: GetPostsUseCase,
    private val appDatabase: AppDatabase,
) : ViewModel() {
    val mutableState = mutableStateOf(PostsState())
    val state by mutableState
    var savedState = savedStateHandle[Constants.STATE_KEY_POSTS] ?: PostsSavedState()

    var scrollJob: Job? = null
    private var sessionPosts = listOf<Post>()

    inline fun updateState(body: (PostsState) -> PostsState) {
        mutableState.value = body(state)
    }

    fun getPosts(
        searchTags: String,
        refresh: Boolean,
        safeListingOnly: Boolean,
    ) {
        if (refresh) {
            updateState {
                it.copy(
                    page = 0,
                    newSearch = true,
                )
            }
        } else {
            updateState {
                it.copy(
                    page = it.page + 1,
                    newSearch = false,
                )
            }
        }

        getPostsUseCase(
            pageId = state.page,
            searchTags = if (safeListingOnly) "$searchTags rating:safe" else searchTags,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    if (refresh) {
                        state.posts.clear()
                    }

                    updateState { it.copy(loading = false) }

                    state.posts.addAll(
                        elements = result.data
                            ?.filter { resultPost ->
                                !state.posts.any { post ->
                                    post.id == resultPost.id
                                }
                            } ?: emptyList(),
                    )
                }
                is Resource.Loading -> {
                    updateState {
                        it.copy(
                            loading = true,
                            error = "",
                        )
                    }
                }
                is Resource.Error -> {
                    updateState { it.copy(error = result.message ?: "An unexpected error occurred") }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getPostsFromSession() {
        viewModelScope.launch(Dispatchers.IO) {
            savedState = PostsSavedState(loadFromSession = false)

            updateState { it.copy(loading = true) }

            sessionPosts = appDatabase.sessionDao()
                .getAll()
                .map { it.toPost() }

            state.posts.clear()
            state.posts.addAll(elements = sessionPosts)

            updateState {
                it.copy(
                    jumpToPosition = true,
                    loading = false,
                )
            }
        }
    }

    fun updateSessionPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            if (state.posts.toList() != sessionPosts.toList()) {
                Timber.i("saving session")
                appDatabase.sessionDao().deleteAll()

                val convertedPosts = state.posts.mapIndexed { index, post ->
                    post.toSessionPost(sequence = index)
                }

                sessionPosts = state.posts.toList()
                appDatabase.sessionDao().insert(convertedPosts)
            }
        }
    }

    fun updateSessionPosition(index: Int, offset: Int) {
        savedState = PostsSavedState(
            scrollIndex = index,
            scrollOffset = offset,
        )

        savedStateHandle.set(
            key = Constants.STATE_KEY_POSTS,
            value = savedState,
        )
    }
}