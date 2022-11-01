package com.github.uragiristereo.mejiboard.presentation.image.more.route.info.core

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.SubcomposeLayout
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.github.uragiristereo.mejiboard.common.helper.NumberHelper
import com.github.uragiristereo.mejiboard.data.model.local.preferences.Theme
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.Tag
import com.github.uragiristereo.mejiboard.domain.entity.provider.tag.TagType
import com.github.uragiristereo.mejiboard.presentation.common.theme.MejiboardTheme
import kotlin.math.ceil

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
            WrapTextContent(
                text = item.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(end = 4.dp)
                    .weight(
                        weight = 1f,
                        fill = false,
                    ),
            )

            Text(
                text = NumberHelper.convertToReadable(item.count),
                fontSize = 14.sp,
            )
        }
    }
}

@Composable
fun WrapTextContent(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    fontStyle: FontStyle? = null,
    fontWeight: FontWeight? = null,
    fontFamily: FontFamily? = null,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    textDecoration: TextDecoration? = null,
    textAlign: TextAlign? = null,
    lineHeight: TextUnit = TextUnit.Unspecified,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    onTextLayout: (TextLayoutResult) -> Unit = {},
    style: TextStyle = LocalTextStyle.current,
) {
    SubcomposeLayout(modifier) { constraints ->
        val composable = @Composable { localOnTextLayout: (TextLayoutResult) -> Unit ->
            Text(
                text = text,
                color = color,
                fontSize = fontSize,
                fontStyle = fontStyle,
                fontWeight = fontWeight,
                fontFamily = fontFamily,
                letterSpacing = letterSpacing,
                textDecoration = textDecoration,
                textAlign = textAlign,
                lineHeight = lineHeight,
                overflow = overflow,
                softWrap = softWrap,
                maxLines = maxLines,
                onTextLayout = localOnTextLayout,
                style = style,
            )
        }
        var textWidthOpt: Int? = null
        subcompose("measureView") {
            composable { layoutResult ->
                textWidthOpt = (0 until layoutResult.lineCount)
                    .maxOf { line ->
                        ceil(layoutResult.getLineRight(line) - layoutResult.getLineLeft(line)).toInt()
                    }
            }
        }[0].measure(constraints)
        val textWidth = textWidthOpt!!
        val placeable = subcompose("content") {
            composable(onTextLayout)
        }[0].measure(constraints.copy(minWidth = textWidth, maxWidth = textWidth))

        layout(width = textWidth, height = placeable.height) {
            placeable.place(0, 0)
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
                    name = "hu_tao_(genshin_impact)aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
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