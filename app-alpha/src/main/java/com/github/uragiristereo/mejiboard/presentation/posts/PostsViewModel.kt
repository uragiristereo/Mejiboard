package com.github.uragiristereo.mejiboard.presentation.posts

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.domain.usecase.GetPostsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class PostsViewModel @Inject constructor(
    private val getPostsUseCase: GetPostsUseCase,
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
}