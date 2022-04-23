package com.github.uragiristereo.mejiboard.presentation.search.result

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.presentation.search.core.SearchState

@Composable
fun BrowseButton(
    state: SearchState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(
                top = 12.dp,
                bottom = 12.dp,
                start = 16.dp,
                end = 16.dp,
            ),
    ) {
        Text(
            text = buildAnnotatedString {
                append(text = "Browse: ")

                withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                    append(text = state.parsedQuery.ifEmpty { "All posts" })
                }
            },
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .padding(end = 16.dp),
        )
        Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")
    }
}