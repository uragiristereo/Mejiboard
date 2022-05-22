package com.github.uragiristereo.mejiboard.presentation.search.result

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.presentation.search.core.SearchState

@Composable
fun SearchItem(
    item: Tag,
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
        Icon(imageVector = Icons.Outlined.Search, contentDescription = "Search")

        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(weight = 1f, fill = true)
                .padding(start = 16.dp, end = 16.dp),
        ) {
            Text(
                text = buildAnnotatedString {
                    val newQuery = "${state.delimiter}${item.name}".lowercase()

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                        append(text = if (newQuery.contains(state.boldWord)) state.boldWord else "")
                    }

                    append(text = newQuery.replaceFirst(oldValue = state.boldWord, newValue = ""))
                },
                modifier = Modifier.weight(weight = 0.6f)
            )

            Text(
                text = "%,d".format(item.count),
                textAlign = TextAlign.Right,
                color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
                modifier = Modifier.weight(weight = 0.2f),
            )
        }

        Icon(painter = painterResource(R.drawable.north_west), contentDescription = "Append search")
    }
}