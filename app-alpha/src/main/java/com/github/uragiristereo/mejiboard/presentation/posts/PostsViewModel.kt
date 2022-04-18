package com.github.uragiristereo.mejiboard.presentation.posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
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
    val postsData = mutableStateListOf<Post>()

    private var _postsProgressVisible = mutableStateOf(false)
    val postsProgressVisible by _postsProgressVisible

    private var _page = mutableStateOf(0)
    val page by _page

    var newSearch by mutableStateOf(true)
//    var newSearch = _newSearch

    private var _postsError = mutableStateOf("")
    val postsError by _postsError

    private var sessionData = listOf<Post>()
    var loadFromSession = savedStateHandle.get(Constants.STATE_KEY_SESSION_EXIST) ?: false
        private set
    var sessionIndex = savedStateHandle.get(Constants.STATE_KEY_POST_INDEX) ?: -1
        private set
    var sessionOffset = savedStateHandle.get(Constants.STATE_KEY_POST_OFFSET) ?: -1
        private set
    var toolbarOffsetHeightPx = mutableStateOf(0f)
    var jumpToPosition = false
    var allowPostClick = true

    fun getPosts(
        searchTags: String,
        refresh: Boolean,
        safeListingOnly: Boolean,
    ) {
        if (refresh) {
            _page.value = 0
            postsData.clear()
            newSearch = true
        } else {
            newSearch = false
            _page.value += 1
        }

        getPostsUseCase(
            pageId = page,
            searchTags = if (safeListingOnly) "$searchTags rating:safe" else searchTags,
        ).onEach { result ->
            when (result) {
                is Resource.Success -> {
                    _postsProgressVisible.value = false
                    postsData.addAll(elements = result.data ?: emptyList())
                }
                is Resource.Loading -> {
                    _postsError.value = ""
                    _postsProgressVisible.value = true
                }
                is Resource.Error -> {
                    _postsError.value = result.message ?: "An unexpected error occurred"
                }
            }
        }.launchIn(viewModelScope)
    }

    fun getPostsFromSession() {
        viewModelScope.launch(Dispatchers.IO) {
            loadFromSession = false
            _postsProgressVisible.value = true

            sessionData = appDatabase.sessionDao()
                .getAll()
                .map { it.toPost() }
            postsData.clear()
            postsData.addAll(sessionData)

            jumpToPosition = true
            _postsProgressVisible.value = false
        }
    }

    fun updateSessionPosts() {
        viewModelScope.launch(Dispatchers.IO) {
            if (postsData.toList() != sessionData.toList()) {
                Timber.i("saving session")
                appDatabase.sessionDao().deleteAll()

                val convertedPosts = postsData.mapIndexed { index, post ->
                    post.toSessionPost(sequence = index)
                }

                sessionData = postsData.toList()
                appDatabase.sessionDao().insert(convertedPosts)
            }
        }
    }

    fun updateSessionPosition(index: Int, offset: Int) {
        sessionIndex = index
        sessionOffset = offset
        savedStateHandle.set(Constants.STATE_KEY_POST_INDEX, index)
        savedStateHandle.set(Constants.STATE_KEY_POST_OFFSET, offset)
        savedStateHandle.set(Constants.STATE_KEY_SESSION_EXIST, true)
    }
}