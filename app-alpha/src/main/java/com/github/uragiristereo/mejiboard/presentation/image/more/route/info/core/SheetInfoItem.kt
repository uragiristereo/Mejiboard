package com.github.uragiristereo.mejiboard.presentation.image.more.route.info.core

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.presentation.theme.MejiboardTheme

@Composable
fun SheetInfoItem(
    leadingText: String,
    trailingText: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(
                start = 16.dp,
                top = 8.dp,
                end = 16.dp,
                bottom = 8.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = leadingText,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.7f),
            modifier = Modifier.padding(end = 4.dp),
        )
        Text(
            text = trailingText,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
fun TagInfoItem(
    tag: String,
    count: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier
            .fillMaxWidth()
            .clickable {

            }
            .padding(
                top = 16.dp,
                bottom = 16.dp
            ),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            Modifier.weight(7.6f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painterResource(R.drawable.circle),
                "",
                Modifier
                    .padding(
                        start = 24.dp,
                        end = 24.dp
                    )
                    .size(18.dp)
            )
            Text(
                tag,
                Modifier
            )
        }
        Text(
            "$count",
            Modifier
                .weight(2.4f)
                .padding(end = 16.dp),
            color = MaterialTheme.colors.onSurface.copy(0.5f),
            textAlign = TextAlign.End
        )
    }
}

@Preview
@Composable
fun prev() {
    MejiboardTheme {
        TagInfoItem(tag = "hazawa_tsugumi", count = 1009)
    }
}