package com.github.uragiristereo.mejiboard.ui.screens.search

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.ui.components.SearchView
import com.github.uragiristereo.mejiboard.ui.viewmodel.MainViewModel
import com.github.uragiristereo.mejiboard.ui.viewmodel.SearchViewModel
import com.github.uragiristereo.mejiboard.util.getWordInPosition
import com.google.accompanist.insets.navigationBarsWithImePadding
import com.google.accompanist.insets.statusBarsPadding
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

    var query by remember { mutableStateOf(TextFieldValue(mainViewModel.searchTags, TextRange(mainViewModel.searchTags.length))) }
    val focusRequester = remember { FocusRequester() }
    var searchAllowed by remember { mutableStateOf(true) }
    var startQueryIndex by remember { mutableStateOf(0) }
    var endQueryIndex by remember { mutableStateOf(0) }
    var delimiter by remember { mutableStateOf("") }
    var lastQuery = remember { TextFieldValue("") }
    var wordInCursor by remember { mutableStateOf("") }
    var boldWord by remember { mutableStateOf("") }

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

                    if (searchAllowed && it.text != lastQuery.text) {
                        if (it.selection.end > 0) {
                            val keywords = arrayOf(" ", "{", "}", "~")
                            val match = keywords.filter { keyword ->
                                keyword in it.text[it.selection.end - 1].toString()
                            }

                            if (match.isEmpty()) {
                                val result = getWordInPosition(it.text, it.selection.end)
                                wordInCursor = result.first

                                if (wordInCursor.isNotEmpty() && wordInCursor != "-") {
                                    delimiter = if (wordInCursor.take(1) == "-") "-" else ""
                                    startQueryIndex = result.second
                                    endQueryIndex = result.third
                                    searchViewModel.getTags(wordInCursor)
                                    scope.launch {
                                        columnState.animateScrollToItem(0)
                                    }
                                }
                            } else {
                                searchViewModel.clearSearches()
                                wordInCursor = ""
                            }
                        } else {
                            searchViewModel.clearSearches()
                            wordInCursor = ""
                        }
                    }

                    lastQuery = query
                    query = it
                },
                onBackPressed = {
                    keyboardController!!.hide()
                    mainNavigation.navigateUp()
                    searchAllowed = false
                },
                onQueryTextSubmit = {
                    var submitQuery = it.text
                        .replace("\\s+".toRegex(), " ")
                        .replace("{ ", "{")
                        .replace(" }", "}")

                    if (submitQuery.isNotEmpty())
                        if (submitQuery[submitQuery.length - 1] != ' ')
                            submitQuery = "$submitQuery "

                    if (submitQuery == " ")
                        submitQuery = ""

                    mainViewModel.searchTags = submitQuery.lowercase()
                    mainViewModel.refreshNeeded = true

                    keyboardController!!.hide()
                    mainNavigation.navigate("main") {
                        popUpTo(0)
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
                    .fillMaxSize()
                    .navigationBarsWithImePadding(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                LinearProgressIndicator(
                    Modifier
                        .alpha(if (searchViewModel.searchProgressVisible) 1f else 0f)
                        .fillMaxWidth()
                )
                LazyColumn(
                    Modifier
                        .fillMaxWidth()
                        .weight(1f, true),
                    state = columnState,
//                    verticalArrangement = if (searchViewModel.searchError.isEmpty()) Arrangement.Top else Arrangement.Center
                    verticalArrangement = Arrangement.Top
                ) {
                    var submitQuery = query.text
                        .replace("\\s+".toRegex(), " ")
                        .replace("{ ", "{")
                        .replace(" }", "}")

                    if (submitQuery == " ")
                        submitQuery = ""

                    submitQuery = submitQuery.trim().lowercase()

                    item {
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .clickable(onClick = {
                                    if (submitQuery.isNotEmpty())
                                        if (submitQuery[submitQuery.length - 1] != ' ')
                                            submitQuery = "$submitQuery "

                                    if (submitQuery == " ")
                                        submitQuery = ""

                                    mainViewModel.searchTags = submitQuery
                                    mainViewModel.refreshNeeded = true

                                    keyboardController!!.hide()
                                    mainNavigation.navigate("main") {
                                        popUpTo(0)
                                    }
                                })
                                .padding(
                                    top = 12.dp,
                                    bottom = 12.dp,
                                    start = 16.dp,
                                    end = 16.dp
                                ),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                buildAnnotatedString {
                                    append("Browse: ")
                                    withStyle(
                                        style = SpanStyle(
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(if (submitQuery.isNotEmpty()) submitQuery else "All posts")
                                    }
                                },
                                Modifier
                                    .weight(1f, true)
                                    .padding(end = 16.dp),
                            )
                            Icon(
                                Icons.Outlined.Search,
                                "Search"
                            )
                        }
                    }

                    item { Spacer(Modifier.padding(bottom = 8.dp)) }
                    if (wordInCursor.isNotEmpty()) {
                        if (!searchViewModel.searchProgressVisible)
                            boldWord = wordInCursor.lowercase()

                        itemsIndexed(searchViewModel.searchData) { _, item ->
                            Row(
                                Modifier
                                    .fillMaxWidth()
                                    .clickable(onClick = {
                                        searchAllowed = false

                                        val result = query.text.replaceRange(
                                            startQueryIndex,
                                            endQueryIndex,
                                            "$delimiter${item.value} "
                                        )

                                        val newQuery =
                                            "$result "
                                                .replace("\\s+".toRegex(), " ")

                                        query = TextFieldValue(newQuery, TextRange(newQuery.length))
                                        searchViewModel.clearSearches()
                                        searchAllowed = true
                                    })
                                    .padding(
                                        top = 12.dp,
                                        bottom = 12.dp,
                                        start = 16.dp,
                                        end = 16.dp
                                    ),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Outlined.Search,
                                    "Search"
                                )
                                Row(
                                    Modifier
                                        .weight(1f, true)
                                        .padding(start = 16.dp, end = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        buildAnnotatedString {
                                            val newQuery = "$delimiter${item.value}".lowercase()

                                             withStyle(
                                                 style = SpanStyle(fontWeight = FontWeight.Bold)
                                             ) {
                                                 append(if (newQuery.contains(boldWord)) boldWord else "")
                                             }

                                            append(newQuery.replace(boldWord, ""))
                                        },
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
                                )
                            }
                        }
                    }

                    item { Spacer(Modifier.padding(bottom = 8.dp)) }

                    if (searchViewModel.searchError.isNotEmpty()) {
                        item {
                            Column(
                                Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(Icons.Outlined.Warning, null)
                                Text(
                                    "Error:\n(${searchViewModel.searchError})",
                                    modifier = Modifier.padding(top = 16.dp),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }

                val minWidth = 48.dp
                Card(
                    elevation = 2.dp,
                    shape = RectangleShape,
                ) {
                    LazyRow(
                        Modifier
                            .fillMaxWidth()
                    ) {
                        item {
                            TextButton(
                                onClick = { query = TextFieldValue("") },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("CLEAR") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, " "),
                                        TextRange(queryStartIndex + 1)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("SPACE") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, "_"),
                                        TextRange(queryStartIndex + 1)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("_") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, "-"),
                                        TextRange(queryStartIndex + 1)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("-") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, ":"),
                                        TextRange(queryStartIndex + 1)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text(":") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, "("),
                                        TextRange(queryStartIndex + 1)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("(") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, ")"),
                                        TextRange(queryStartIndex + 1)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text(")") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    var replacement = "{"

                                    if (queryStartIndex > 0)
                                        replacement = if (previousQuery[queryStartIndex - 1] == ' ') "{" else " {"

                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, replacement),
                                        TextRange(queryStartIndex + replacement.length)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("{") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    var queryStartIndex = query.selection.start
                                    val queryEndIndex = query.selection.end
                                    val previousQuery = query.text
                                    var replacement = "} "

                                    if (queryStartIndex < previousQuery.length)
                                        replacement = if (previousQuery[queryStartIndex] == ' ') "}" else "} "

                                    if (queryStartIndex > 0)
                                        if (previousQuery[queryStartIndex - 1] == ' ')
                                            queryStartIndex -= 1

                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryEndIndex, replacement),
                                        TextRange(queryStartIndex + replacement.length)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("}") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    var replacement = "~ "

                                    if (queryStartIndex < previousQuery.length)
                                        replacement = if (previousQuery[queryStartIndex] == ' ') "~" else "~ "

                                    if (queryStartIndex > 0)
                                        if (previousQuery[queryStartIndex - 1] != ' ')
                                            replacement = " $replacement"

                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, replacement),
                                        TextRange(queryStartIndex + replacement.length)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("~") }
                        }
                        item { Divider(
                            Modifier
                                .height(36.dp)
                                .width(1.dp)) }
                        item {
                            TextButton(
                                onClick = {
                                    val queryStartIndex = query.selection.start
                                    val previousQuery = query.text
                                    query = TextFieldValue(
                                        previousQuery.replaceRange(queryStartIndex, queryStartIndex, "*"),
                                        TextRange(queryStartIndex + 1)
                                    )
                                },
                                Modifier
                                    .widthIn(minWidth)
                                    .height(36.dp)
                            ) { Text("*") }
                        }
                    }
                }
            }
        }
    }
}