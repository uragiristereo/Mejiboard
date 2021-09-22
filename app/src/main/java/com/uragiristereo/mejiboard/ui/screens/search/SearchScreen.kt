package com.uragiristereo.mejiboard.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Warning
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
import com.uragiristereo.mejiboard.R
import com.uragiristereo.mejiboard.ui.components.SearchView
import com.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.uragiristereo.mejiboard.ui.viewmodel.SearchViewModel
import com.uragiristereo.mejiboard.util.getWordInPosition
import kotlinx.coroutines.launch

@ExperimentalComposeUiApi
@Composable
fun SearchScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
    searchViewModel: SearchViewModel = hiltViewModel(),
) {
    val keyboardController = LocalSoftwareKeyboardController.current
    val scope = rememberCoroutineScope()
    val columnState = rememberLazyListState()

    val query = remember { mutableStateOf(TextFieldValue(mainViewModel.searchTags, TextRange(mainViewModel.searchTags.length))) }
    val focusRequester = remember { FocusRequester() }
    var searchAllowed by remember { mutableStateOf(true) }
    var startQueryIndex by remember { mutableStateOf(0) }
    var endQueryIndex by remember { mutableStateOf(0) }
    var delimiter by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            SearchView(
                modifier = Modifier
                    .focusRequester(focusRequester)
                    .statusBarsPadding(),
                query = query,
                placeholder = "Example: 1girl blue_hair",
                onQueryTextChange = {
                    if (it.text == "")
                        searchViewModel.clearSearches()

                    if (query.value.text != it.text) {
                        query.value = it

                        if (searchAllowed) {
                            if (it.selection.end > 0) {
                                val keywords = arrayOf(" ", "{", "}", "~")
                                val match = keywords.filter { keyword ->
                                    keyword in it.text[it.selection.end - 1].toString()
                                }

                                if (match.isEmpty()) {
                                    val result = getWordInPosition(it.text, it.selection.end)
                                    if (result.first.isNotEmpty() && result.first != "-") {
                                        delimiter = if (result.first.take(1) == "-") "-" else ""
                                        startQueryIndex = result.second
                                        endQueryIndex = result.third
                                        searchViewModel.getTags(result.first)
                                        scope.launch {
                                            columnState.animateScrollToItem(0)
                                        }
                                    }
                                } else searchViewModel.clearSearches()
                            } else searchViewModel.clearSearches()
                        } else searchAllowed = true
                    }
                },
                onBackPressed = {
                    keyboardController!!.hide()
                    mainNavigation.navigateUp()
                },
                onQueryTextSubmit = {
                    val submitQuery = it.text.replace("\\s+".toRegex(), " ")

                    mainViewModel.searchTags = submitQuery
                    mainViewModel.refreshNeeded = true

                    keyboardController!!.hide()
//                    mainNavigation.navigate("main") {
//                        popUpTo("splash") { inclusive = true }
//                        launchSingleTop = true
//                    }
//                    mainNavigation.navigateUp()
                    mainNavigation.popBackStack()
                    mainNavigation.navigate("main") {
                        popUpTo("main") { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )

            DisposableEffect(Unit) {
                focusRequester.requestFocus()
                keyboardController!!.show()
                onDispose { }
            }
        }
    ) {
        Surface(
            Modifier.padding(it)
        ) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .navigationBarsWithImePadding()
            ) {
                LinearProgressIndicator(
                    Modifier
                        .alpha(if (searchViewModel.searchProgressVisible) 1f else 0f)
                        .fillMaxWidth()
                )
                if (searchViewModel.searchError == "") {
                    if (query.value.text.isNotEmpty()) {
                        LazyColumn(
                            Modifier
                                .fillMaxWidth(),
                            state = columnState
                        ) {
                            itemsIndexed(searchViewModel.searchData) { _, item ->
                                Row(
                                    Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = {
                                            searchAllowed = false

                                            val result = query.value.text.replaceRange(
                                                startQueryIndex,
                                                endQueryIndex,
                                                "$delimiter${item.value} "
                                            )

                                            val newQuery = "$result ".replace("\\s+".toRegex(), " ")

                                            query.value = TextFieldValue(newQuery, TextRange(newQuery.length))
                                            searchViewModel.clearSearches()
                                            searchAllowed = true
                                        })
                                        .padding(16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Icon(
                                        Icons.Outlined.Search,
                                        "Search",
                                        Modifier
                                            .weight(1f)
                                    )
                                    Row(
                                        Modifier
                                            .weight(8f)
                                            .padding(start = 16.dp, end = 16.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            "$delimiter${item.value}",
                                            Modifier.weight(6f)
                                        )
                                        Text(
                                            item.post_count.toString(),
                                            Modifier.weight(2f),
                                            textAlign = TextAlign.Right,
                                            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f)
                                        )
                                    }
                                    Icon(
                                        painterResource(R.drawable.north_west),
                                        "Append search",
                                        Modifier
                                            .weight(1f)
                                    )
                                }
                            }
                        }
                    }
                } else {
                    Column(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Outlined.Warning,
                            "Error",
                            Modifier.padding(16.dp)
                        )
                        Text(
                            "Error:\n(${searchViewModel.searchError})",
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}