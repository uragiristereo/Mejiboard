package com.github.uragiristereo.mejiboard.presentation.image.more.route.info.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.uragiristereo.mejiboard.common.helper.NumberHelper
import com.github.uragiristereo.mejiboard.data.preferences.enums.Theme
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagType
import com.github.uragiristereo.mejiboard.presentation.theme.MejiboardTheme

@Composable
fun TagItem(
    item: Tag,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val itemColors = TagColors.colors[item.type]!!

    Surface(
        contentColor = when {
            MaterialTheme.colors.isLight -> itemColors.lightColor
            else -> itemColors.darkColor
        }.copy(alpha = 0.87f),
        color = when {
            MaterialTheme.colors.isLight -> itemColors.lightBackgroundColor
            else -> itemColors.darkBackgroundColor
        }.copy(alpha = 0.4f),
        shape = RoundedCornerShape(percent = 50),
        modifier = modifier,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .clickable(onClick = onClick)
                .padding(
                    horizontal = 12.dp,
                    vertical = 6.dp,
                ),
        ) {
            Text(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(end = 4.dp),
            )

            Text(
                text = NumberHelper.convertToReadable(item.count),
                fontSize = 14.sp,
            )
        }
    }
}

@Preview
@Composable
fun TagItemPreview() {
    MejiboardTheme(
        theme = Theme.Dark,
    ) {
        Column(
            modifier = Modifier.background(color = MaterialTheme.colors.background),
        ) {
            TagItem(
                item = Tag(
                    id = 0,
                    name = "sesield",
                    count = 146,
                    type = TagType.ARTIST,
                ),
                onClick = { },
                modifier = Modifier.padding(bottom = 8.dp),
            )

            TagItem(
                item = Tag(
                    id = 0,
                    name = "hu_tao_(genshin_impact)",
                    count = 5023,
                    type = TagType.CHARACTER,
                ),
                onClick = { },
                modifier = Modifier.padding(bottom = 8.dp),
            )

            TagItem(
                item = Tag(
                    id = 0,
                    name = "genshin_impact",
                    count = 72940,
                    type = TagType.COPYRIGHT,
                ),
                onClick = { },
                modifier = Modifier.padding(bottom = 8.dp),
            )

            TagItem(
                item = Tag(
                    id = 0,
                    name = "1girl",
                    count = 3205287,
                    type = TagType.GENERAL,
                ),
                onClick = { },
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
    }
}