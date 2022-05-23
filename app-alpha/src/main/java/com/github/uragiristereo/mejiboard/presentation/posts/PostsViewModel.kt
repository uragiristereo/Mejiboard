package com.github.uragiristereo.mejiboard.presentation.posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.common.Constants
import com.github.uragiristereo.mejiboard.data.local.database.AppDatabase
import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Post
import com.github.uragiristereo.mejiboard.domain.usecase.api.GetPostsUseCase
import com.github.uragiristereo.mejiboard.presentation.posts.core.PostsSavedState
import com.github.uragiristereo.mejiboard.presentation.posts.core.PostsState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
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

    private var postsJob: Job? = null
    private var sessionPosts = listOf<Post>()

    inline fun updateState(body: (PostsState) -> PostsState) {
        mutableState.value = body(state)
    }

    fun retryGetPosts() {
        fetchPosts(tags = state.tags, refresh = false)
    }

    private fun fetchPosts(
        tags: String,
        refresh: Boolean,
        onLoaded: () -> Unit = { },
    ) {
        updateState { it.copy(error = "") }

        postsJob?.cancel()
        postsJob = viewModelScope.launch {
            getPostsUseCase(
                provider = state.selectedProvider,
                tags = tags,
                pageId = state.page,
                onLoading = { loading ->
                    updateState { it.copy(loading = loading) }
                },
                onSuccess = { result, canLoadMore ->
                    if (refresh) {
                        state.posts.clear()
                    }

                    onLoaded()

                    updateState {
                        it.copy(
                            error = "",
                            canLoadMore = canLoadMore,
                        )
                    }

                    state.posts.addAll(
                        elements = result
                            .filter { resultPost ->
                                !state.posts.any { post ->
                                    post.id == resultPost.id
                                }
                            },
                    )
                },
                onFailed = { msg ->
                    updateState { it.copy(error = msg) }
                },
                onError = { t ->
                    updateState { it.copy(error = t.toString()) }
                },
            )
        }
    }

    fun getPosts(
        refresh: Boolean,
        onLoaded: () -> Unit = { },
    ) {
        updateState {
            it.copy(
                page = if (refresh) 0 else it.page + 1,
            )
        }

        if (refresh) {
            state.posts.clear()
        }

        fetchPosts(
            tags = state.tags,
            refresh = refresh,
            onLoaded = onLoaded,
        )
    }

    fun getPostsFromSession() {
        viewModelScope.launch(Dispatchers.IO) {
//            savedState = savedState.copy(loadFromSession = false)
//            updateState { it.copy(loading = true) }
//
//            sessionPosts = appDatabase.sessionDao()
//                .getAll()
//                .map { it.toPost() }
//
//            state.posts.clear()
//            state.posts.addAll(elements = sessionPosts)
//
//            updateState {
//                it.copy(
//                    jumpToPosition = true,
//                    loading = false,
//                )
//            }
        }
    }

    fun updateSessionPosts() {
        viewModelScope.launch(Dispatchers.IO) {
//            if (state.posts.toList() != sessionPosts.toList()) {
//                appDatabase.sessionDao().deleteAll()
//
//                val convertedPosts = state.posts.mapIndexed { index, post ->
//                    post.toSessionPost(sequence = index)
//                }
//
//                sessionPosts = state.posts.toList()
//                appDatabase.sessionDao().insert(convertedPosts)
//            }
        }
    }

    fun updateSessionPosition(index: Int, offset: Int) {
        savedState = savedState.copy(
            scrollIndex = index,
            scrollOffset = offset,
        )

        savedStateHandle[Constants.STATE_KEY_POSTS] = savedState.copy(loadFromSession = true)
    }
}