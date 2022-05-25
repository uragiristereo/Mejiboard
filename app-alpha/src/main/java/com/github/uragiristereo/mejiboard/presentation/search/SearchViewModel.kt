package com.github.uragiristereo.mejiboard.presentation.search

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.TextFieldValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.uragiristereo.mejiboard.common.RatingFilter
import com.github.uragiristereo.mejiboard.domain.usecase.api.SearchTermUseCase
import com.github.uragiristereo.mejiboard.presentation.search.core.SearchState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchTermUseCase: SearchTermUseCase,
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
                searchTermUseCase(
                    provider = _state.selectedProvider,
                    filters = RatingFilter.GENERAL_ONLY,
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

    private fun getWordInPosition(text: String, position: Int): Triple<String, Int, Int> {
        val delimiters = arrayOf(' ', '{', '}')

        val prevIndexes = IntArray(delimiters.size)
        val nextIndexes = IntArray(delimiters.size)

        delimiters.forEachIndexed { index, delimiter ->
            prevIndexes[index] = text.lastIndexOf(char = delimiter, startIndex = position - 1)
        }

        delimiters.forEachIndexed { index, delimiter ->
            nextIndexes[index] = when {
                text.indexOf(char = delimiter, startIndex = position) != -1 -> text.indexOf(delimiter, position)
                else -> text.length
            }
        }

        val prevDelimiterIndex = prevIndexes.maxOrNull()!!
        val nextDelimiterIndex =
            when {
                nextIndexes.minOrNull()!! != text.length -> nextIndexes.minOrNull()!!
                else -> -1
            }
        var startIndex =
            when {
                prevDelimiterIndex < 0 -> 0
                else -> prevDelimiterIndex + 1
            }
        val endIndex =
            when {
                nextDelimiterIndex < 0 -> text.length
                else -> nextDelimiterIndex
            }

        if (startIndex >= endIndex) {
            startIndex = 0
        }

        val word = text.substring(startIndex = startIndex, endIndex = endIndex)

        return Triple(first = word, second = startIndex, third = endIndex)
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
                val result = getWordInPosition(
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
        var submitQuery = query
            .replace("\\s+".toRegex(), " ")
            .replace("{ ", "{")
            .replace(" }", "}")

        if (submitQuery.isNotEmpty()) {
            if (submitQuery[submitQuery.length - 1] != ' ')
                submitQuery = "$submitQuery "
        }

        if (submitQuery == " ") {
            submitQuery = ""
        }

        _state = _state.copy(parsedQuery = submitQuery.lowercase())
    }

    fun cancelSearch() {
        job?.cancel()
    }
}