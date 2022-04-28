package com.github.uragiristereo.mejiboard.presentation.search

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
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
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.search.core.SearchView
import com.github.uragiristereo.mejiboard.presentation.search.quickshortcutbar.QuickShortcutBar
import com.github.uragiristereo.mejiboard.presentation.search.result.SearchResult
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
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
                text = mainViewModel.searchTags,
                selection = TextRange(mainViewModel.searchTags.length),
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
                    viewModel.updateState(updatedState = state.copy(searchAllowed = false))
                },
                onQueryTextSubmit = {
                    viewModel.parseSearchQuery(it.text)
                    mainViewModel.refreshNeeded = true
                    mainViewModel.saveSearchTags(state.parsedQuery)

                    keyboardController?.hide()
                    mainNavigation.navigate(route = "main") {
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
//            LinearProgressIndicator(
//                modifier = Modifier
//                    .alpha(if (state.searchProgressVisible) 1f else 0f)
//                    .fillMaxWidth()
//            )

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