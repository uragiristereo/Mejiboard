package com.github.uragiristereo.mejiboard.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp


@Composable
fun DrawerItem(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    selected: Boolean = false,
    icon: ImageVector,
    text: String,
    darkTheme: Boolean = false
) {
    val color: Color
    val contentColor: Color

    if (selected) {
        if (darkTheme) {
            color = MaterialTheme.colors.surface.copy(0.3f)
            contentColor = MaterialTheme.colors.primary
        } else {
            color = MaterialTheme.colors.primaryVariant.copy(0.3f)
            contentColor = MaterialTheme.colors.primaryVariant
        }
    } else {
        color = MaterialTheme.colors.surface
        contentColor = MaterialTheme.colors.onSurface
    }

    Surface(
        shape = RoundedCornerShape(4.dp),
        color = color,
        contentColor = contentColor,
        modifier = modifier
            .fillMaxWidth()
//            .padding(8.dp),
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp),
    ) {
        Row(
            Modifier
                .clickable(onClick = onClick)
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                icon,
                text,
                Modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp
                    )
            )
            Text(
                text,
                Modifier
                    .padding(
                        start = 8.dp,
                        end = 8.dp
                    )
            )
        }
    }
}

@Preview
@Composable
fun Preview() {
    Column {
        DrawerItem(
            onClick = { /*TODO*/ },
            icon = Icons.Default.Home,
            text = "Home",
            selected = true
        )
        DrawerItem(
            onClick = { /*TODO*/ },
            icon = Icons.Default.Settings,
            text = "Settings"
        )
    }
}