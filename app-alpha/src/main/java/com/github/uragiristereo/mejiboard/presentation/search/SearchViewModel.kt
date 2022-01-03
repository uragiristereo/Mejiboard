package com.github.uragiristereo.mejiboard.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Search
import com.github.uragiristereo.mejiboard.domain.usecase.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import java.util.*
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase,
) : ViewModel() {
    var searchData: List<Search> by mutableStateOf(listOf())
    var searchProgressVisible by mutableStateOf(false)
    var searchError by mutableStateOf("")
    var tagsQueue: ArrayList<String> = arrayListOf()

    fun getTags(newTag: String) {
        if (newTag != "") {
            searchError = ""
            val thisUUID = UUID.randomUUID().toString()

            tagsQueue.add(thisUUID)

            getTagsUseCase(newTag)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            searchProgressVisible = false
                            searchData = result.data ?: emptyList()
                        }
                        is Resource.Loading -> {
                            searchError = ""
                            searchProgressVisible = true
                        }
                        is Resource.Error -> {
                            searchError = result.message ?: "An unexpected error occurred"
                        }
                    }
                }
                .launchIn(viewModelScope)
        } else clearSearches()
    }

    fun clearSearches() {
        searchData = listOf()
        searchProgressVisible = false
    }
}