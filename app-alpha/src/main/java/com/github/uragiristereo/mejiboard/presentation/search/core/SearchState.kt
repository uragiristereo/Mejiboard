package com.github.uragiristereo.mejiboard.presentation.search.core

import com.github.uragiristereo.mejiboard.domain.entity.Search

data class SearchState(
    val searchData: List<Search> = emptyList(),
    val searchProgressVisible: Boolean = false,
    val searchError: String = "",
    val searchAllowed: Boolean = false,
    val startQueryIndex: Int = 0,
    val endQueryIndex: Int = 0,
    val delimiter: String = "",
    val wordInCursor: String = "",
    val boldWord: String = "",
    val parsedQuery: String = "",
)
