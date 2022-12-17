package com.github.uragiristereo.mejiboard.presentation.posts.appbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomAppBar
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.presentation.main.core.MainRoute


@ExperimentalMaterialApi
@Composable
fun PostsBottomAppBar(
    tags: String,
    moreDropDownExpanded: Boolean,
    onNavigate: (String) -> Unit,
    onDropDownExpandedChange: (Boolean) -> Unit,
    onDropDownClicked: (String) -> Unit,
    onMenuClicked: () -> Unit,
) {

    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
        contentPadding = when (WindowInsets.navigationBars.asPaddingValues().calculateEndPadding(LocalLayoutDirection.current)) {
            0.dp -> WindowInsets.navigationBars.asPaddingValues()
            else -> PaddingValues()
        },
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            IconButton(
                onClick = onMenuClicked,
                content = {
                    Icon(
                        imageVector = Icons.Default.Menu,
                        contentDescription = null,
                    )
                },
            )

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(weight = 1f),
            ) {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary
                        .copy(
                            alpha = when {
                                MaterialTheme.colors.isLight -> 0.4f
                                else -> 0.2f
                            },
                        ),
                    modifier = Modifier
                        .width(128.dp)
                        .height(48.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier.clickable {
                            onNavigate(MainRoute.Search.parseRoute(value = tags))
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                        )

                        Text(text = "Search".uppercase())
                    }
                }
            }

            IconButton(
                onClick = {
                    onDropDownExpandedChange(true)
                },
                content = {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = null,
                    )

                    DropdownMenu(
                        expanded = moreDropDownExpanded,
                        onDismissRequest = {
                            onDropDownExpandedChange(false)
                        },
                        content = {
                            DropdownMenuItem(
                                onClick = {
                                    onDropDownClicked("go_top")
                                },
                                content = {
                                    Text(text = "Go to top")
                                },
                            )

                            DropdownMenuItem(
                                onClick = {
                                    onDropDownClicked("refresh")
                                },
                                content = {
                                    Text(text = "Refresh")
                                },
                            )

                            DropdownMenuItem(
                                onClick = {
                                    onDropDownClicked("all_post")
                                },
                                content = {
                                    Text(text = "All posts")
                                },
                            )
                        },
                    )
                },
            )
        }
    }
}