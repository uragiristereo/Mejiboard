package com.github.uragiristereo.mejiboard.presentation.posts.appbar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.presentation.main.LocalFixedInsets
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.github.uragiristereo.mejiboard.presentation.posts.PostsViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.launch


@ExperimentalMaterialApi
@Composable
fun PostsBottomAppBar(
    mainNavigation: NavHostController,
    drawerState: ModalBottomSheetState,
    dropDownExpanded: Boolean,
    onDropDownExpandedChange: (Boolean) -> Unit,
    onToolbarOffsetHeightPxChange: (Float) -> Unit,
    mainViewModel: MainViewModel,
    postsViewModel: PostsViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val systemUiController = rememberSystemUiController()
    val preferences = mainViewModel.preferences

    BottomAppBar(
        backgroundColor = MaterialTheme.colors.surface.copy(alpha = 0.95f),
        contentPadding = LocalFixedInsets.current.navigationBarsPadding,
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth(),
        ) {
            val navigationBarColor = MaterialTheme.colors.surface.copy(alpha = 0.4f)

            IconButton(
                onClick = {
                    systemUiController.setNavigationBarColor(navigationBarColor)
                    scope.launch { drawerState.show() }
                },
            ) { Icon(imageVector = Icons.Default.Menu, contentDescription = null) }

            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.weight(weight = 1f),
            ) {
                Surface(
                    shape = RoundedCornerShape(percent = 50),
                    color = Color.Transparent,
                    contentColor = MaterialTheme.colors.primary.copy(alpha = if (MaterialTheme.colors.isLight) 0.4f else 0.2f),
                    modifier = Modifier
                        .width(128.dp)
                        .height(48.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly,
                        modifier = Modifier
                            .clickable { mainNavigation.navigate("search") },
                    ) {
                        Icon(imageVector = Icons.Default.Search, contentDescription = null)

                        Text(text = "Search".uppercase())
                    }
                }
            }

            IconButton(
                onClick = { onDropDownExpandedChange(true) },
            ) {
                Icon(imageVector = Icons.Default.MoreVert, contentDescription = null)

                DropdownMenu(
                    expanded = dropDownExpanded,
                    onDismissRequest = { onDropDownExpandedChange(false) }
                ) {
                    DropdownMenuItem(
                        onClick = {
                            onDropDownExpandedChange(false)
                            postsViewModel.getPosts(mainViewModel.searchTags, true, preferences.safeListingOnly)
                            onToolbarOffsetHeightPxChange(0f)
                        },
                        content = { Text(text = "Refresh") }
                    )

                    DropdownMenuItem(
                        onClick = {
                            onDropDownExpandedChange(false)
                            mainViewModel.saveSearchTags(query = "")
                            postsViewModel.getPosts(mainViewModel.searchTags, true, preferences.safeListingOnly)
                            onToolbarOffsetHeightPxChange(0f)
                        },
                        content = { Text(text = "All posts") }
                    )
                }
            }
        }
    }
}