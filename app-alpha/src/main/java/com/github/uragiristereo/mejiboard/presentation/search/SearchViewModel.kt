package com.github.uragiristereo.mejiboard.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.data.model.Resource
import com.github.uragiristereo.mejiboard.domain.entity.Search
import com.github.uragiristereo.mejiboard.domain.usecase.GetTagsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase,
) : ViewModel() {
    val query = mutableStateOf(TextFieldValue(text = ""))
    var searchData: List<Search> by mutableStateOf(listOf())
        private set
    var searchProgressVisible by mutableStateOf(false)
        private set
    var searchError by mutableStateOf("")
        private set
    private var job: Job? = null

    fun getTags(newTag: String) {
        if (newTag != "") {
            searchError = ""
            job?.cancel()

            job = getTagsUseCase(newTag)
                .onEach { result ->
                    when (result) {
                        is Resource.Success -> {
                            searchProgressVisible = false
                            searchError = ""
                            searchData = result.data ?: emptyList()
                        }
                        is Resource.Loading -> {
                            searchProgressVisible = true
                        }
                        is Resource.Error -> {
                            searchProgressVisible = false
                            searchError = result.message ?: "An unexpected error occurred"
                        }
                    }
                }
                .launchIn(viewModelScope)
        } else clearSearches()
    }

    fun clearSearches() {
        searchData = listOf()
        searchError = ""
        searchProgressVisible = false
    }
}