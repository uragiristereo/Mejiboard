package com.github.uragiristereo.mejiboard.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.common.util.SearchUtil
import com.github.uragiristereo.mejiboard.domain.usecase.api.GetTagsUseCase
import com.github.uragiristereo.mejiboard.presentation.search.core.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val getTagsUseCase: GetTagsUseCase,
) : ViewModel() {
    val state = mutableStateOf(SearchState())

    private var _state by state
    private var job: Job? = null
    private val keywords = arrayOf(' ', '{', '}', '~')

    fun updateState(updatedState: SearchState) {
        _state = updatedState
    }

    private fun getTags(term: String) {
        if (term.isNotEmpty()) {
            _state = _state.copy(searchError = "")
            job?.cancel()

            job = viewModelScope.launch {
                getTagsUseCase(
                    term = term,
                    onLoading = { loading ->
                        _state = _state.copy(searchProgressVisible = loading)
                    },
                    onSuccess = { data ->
                        _state = _state.copy(
                            searchData = data,
                            searchError = "",
                        )
                    },
                    onFailed = { message ->
                        _state = _state.copy(
                            searchData = emptyList(),
                            searchError = message,
                        )
                    },
                    onError = { t ->
                        _state = _state.copy(
                            searchData = emptyList(),
                            searchError = t.toString(),
                        )
                    },
                )
            }
        } else clearSearches()
    }

    fun searchTerm(
        textField: TextFieldValue,
        onDoSearch: () -> Unit,
    ) {
        if (textField.selection.end > 0) {
            val match = keywords.filter { keyword ->
                keyword in textField.text[textField.selection.end - 1].toString()
            }

            if (match.isEmpty()) {
                val result = SearchUtil.getWordInPosition(
                    text = textField.text,
                    position = textField.selection.end,
                )
                _state = _state.copy(wordInCursor = result.first)

                if (_state.wordInCursor.isNotEmpty() && _state.wordInCursor != "-") {

                    _state = _state.copy(
                        delimiter = when {
                            _state.wordInCursor.take(1) == "-" -> "-"
                            else -> ""
                        },
                        startQueryIndex = result.second,
                        endQueryIndex = result.third,
                    )

                    getTags(_state.wordInCursor)

                    onDoSearch()
                }
            } else {
                clearSearches()
                _state = _state.copy(wordInCursor = "")
            }
        } else {
            clearSearches()
            _state = _state.copy(wordInCursor = "")
        }
    }

    fun clearSearches() {
        _state = _state.copy(
            searchData = emptyList(),
            searchError = "",
            searchProgressVisible = false,
        )
    }

    fun parseSearchQuery(query: String) {
        _state = _state.copy(
            parsedQuery = SearchUtil.parseSearchQuery(query = query),
        )
    }
}