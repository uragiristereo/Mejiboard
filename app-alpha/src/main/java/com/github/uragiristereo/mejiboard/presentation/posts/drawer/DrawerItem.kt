package com.github.uragiristereo.mejiboard.presentation.posts.drawer

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp


@Composable
fun DrawerItem(
    text: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    selected: Boolean = false,
) {
    val color: Color
    val contentColor: Color

    if (selected) {
        if (!MaterialTheme.colors.isLight) {
            color = MaterialTheme.colors.primary.copy(alpha = 0.2f)
            contentColor = MaterialTheme.colors.primary
        } else {
            color = MaterialTheme.colors.primary.copy(alpha = 0.4f)
            contentColor = MaterialTheme.colors.onSurface
        }
    } else {
        color = MaterialTheme.colors.surface
        contentColor = MaterialTheme.colors.onSurface
    }

    Surface(
        color = color,
        contentColor = contentColor,
        shape = RoundedCornerShape(
            topStartPercent = 50,
            bottomStartPercent = 50,
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(
                start = 12.dp,
                top = 2.dp,
                bottom = 2.dp,
            ),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .height(48.dp)
                .clickable(onClick = onClick),
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                modifier = Modifier
                    .padding(
                        start = 16.dp,
                        end = 12.dp,
                    ),
            )

            Text(
                text = text,
                modifier = Modifier.padding(end = 12.dp),
            )
        }
    }
}