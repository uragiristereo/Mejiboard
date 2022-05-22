package com.github.uragiristereo.mejiboard.presentation.search.result

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.main.core.MainRoute
import com.github.uragiristereo.mejiboard.presentation.search.SearchViewModel
import com.github.uragiristereo.mejiboard.presentation.search.core.SearchState

@ExperimentalComposeUiApi
@Composable
fun SearchResult(
    state: SearchState,
    columnState: LazyListState,
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    LazyColumn(
        state = columnState,
        verticalArrangement = Arrangement.Top,
        modifier = modifier.fillMaxWidth(),
    ) {
        item {
            BrowseButton(
                state = state,
                onClick = {
                    mainViewModel.refreshNeeded = true
                    viewModel.parseSearchQuery(query.text)
                    keyboardController?.hide()

                    mainNavigation.navigate(
                        route = MainRoute.Posts.parseRoute(value = state.parsedQuery),
                    ) {
                        popUpTo(id = 0)
                    }
                },
            )
        }

        item { Spacer(Modifier.padding(bottom = 8.dp)) }

        if (state.wordInCursor.isNotEmpty()) {
            if (!state.searchProgressVisible)
                viewModel.updateState(updatedState = state.copy(boldWord = state.wordInCursor.lowercase()))

            items(items = state.searchData) { item ->
                SearchItem(
                    item = item,
                    state = state,
                    onClick = {
                        viewModel.updateState(updatedState = state.copy(searchAllowed = false))

                        val result = query.text.replaceRange(
                            state.startQueryIndex,
                            state.endQueryIndex,
                            "${state.delimiter}${item.name} "
                        )

                        val newQuery = "$result ".replace("\\s+".toRegex(), " ")

                        onQueryChange(TextFieldValue(newQuery, TextRange(newQuery.length)))
                        viewModel.clearSearches()
                        viewModel.updateState(updatedState = state.copy(searchAllowed = true))
                    }
                )
            }
        }

        item { Spacer(Modifier.padding(bottom = 8.dp)) }

        if (state.searchError.isNotEmpty())
            item { SearchError(state = state) }
    }
}