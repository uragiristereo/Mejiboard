package com.github.uragiristereo.mejiboard.presentation.search

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.common.navigation.navigate
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.main.core.MainRoute
import com.github.uragiristereo.mejiboard.presentation.search.core.SearchView
import com.github.uragiristereo.mejiboard.presentation.search.quickshortcutbar.QuickShortcutBar
import com.github.uragiristereo.mejiboard.presentation.search.result.SearchResult
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
    tags: String,
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    viewModel: SearchViewModel = hiltViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()
    val systemUiController = rememberSystemUiController()
    val surfaceColor = MaterialTheme.colors.surface
    val state by viewModel.state

    var query by rememberSaveable(stateSaver = TextFieldValue.Saver) { mutableStateOf(TextFieldValue(text = "")) }
    val focusRequester = remember { FocusRequester() }

    remember(mainViewModel.state.selectedProvider) {
        viewModel.state.update { it.copy(selectedProvider = mainViewModel.state.selectedProvider) }

        true
    }

    DisposableEffect(key1 = Unit) {
        systemUiController.apply {
            if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
                setStatusBarColor(color = Color.Black)
                setNavigationBarColor(color = surfaceColor)
            } else
                setSystemBarsColor(color = surfaceColor)
        }

        if (query.text.isEmpty()) {
            query = TextFieldValue(
                text = tags,
                selection = TextRange(tags.length),
            )
        } else {
            viewModel.parseSearchQuery(query = query.text)
            viewModel.searchTerm(
                textField = query,
                onDoSearch = {
                    scope.launch { columnState.animateScrollToItem(index = 0) }
                },
            )
        }

        onDispose { }
    }

    DisposableEffect(key1 = viewModel) {
        onDispose {
            viewModel.cancelSearch()
        }
    }

    DisposableEffect(key1 = query.text) {
        if (query.text.isEmpty()) {
            viewModel.clearSearches()
        }

        viewModel.parseSearchQuery(query = query.text)
        viewModel.searchTerm(
            textField = query,
            onDoSearch = {
                scope.launch { columnState.animateScrollToItem(index = 0) }
            },
        )

        onDispose { }
    }

    DisposableEffect(key1 = state.searchData) {
        if (query.text.isEmpty()) {
            viewModel.clearSearches()
        }

        onDispose { }
    }

    Scaffold(
        topBar = {
            SearchView(
                query = query,
                focusRequester = focusRequester,
                onQueryTextChange = {
                    if (state.searchAllowed && it.text != query.text) {
                        viewModel.searchTerm(
                            textField = it,
                            onDoSearch = {
                                scope.launch { columnState.animateScrollToItem(index = 0) }
                            },
                        )
                    }

                    query = it
                },
                onBackPressed = {
                    keyboardController?.hide()
                    mainNavigation.navigateUp()
                    viewModel.state.update{ it.copy(searchAllowed = false) }
                },
                onQueryTextSubmit = {
                    viewModel.parseSearchQuery(it.text)
                    mainViewModel.triggerRefresh()

                    keyboardController?.hide()
                    viewModel.cancelSearch()

                    mainNavigation.navigate(
                        route = MainRoute.Posts,
                        data = mapOf("tags" to state.parsedQuery)
                    ) {
                        popUpTo(id = 0)
                    }
                },
                placeholder = "Example: 1girl blue_hair",
                loading = state.searchProgressVisible,
            )
        },
        modifier = Modifier
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding(),
    ) { innerPadding ->
        DisposableEffect(key1 = Unit) {
            focusRequester.requestFocus()
            keyboardController?.show()

            onDispose { }
        }

        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.padding(paddingValues = innerPadding),
        ) {
            SearchResult(
                state = state,
                columnState = columnState,
                query = query,
                onQueryChange = { query = it },
                mainNavigation = mainNavigation,
                mainViewModel = mainViewModel,
                modifier = Modifier.weight(weight = 1f, fill = true),
            )

            QuickShortcutBar(
                query = query,
                onQueryChange = { query = it },
            )
        }
    }
}