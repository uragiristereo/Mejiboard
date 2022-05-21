package com.github.uragiristereo.mejiboard.presentation.posts.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.presentation.common.DragHandle
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedNavigationBarsPadding
import com.github.uragiristereo.mejiboard.presentation.main.core.MainRoute
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun PostsBottomDrawer(
    mainNavigation: NavHostController,
    drawerState: ModalBottomSheetState,
) {
    val scope = rememberCoroutineScope()

    var drawerItemSelected by remember { mutableStateOf("home") }

    ModalBottomSheetLayout(
        sheetState = drawerState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        scrimColor = Color(0xFF121212).copy(alpha = DrawerDefaults.ScrimOpacity),
        sheetElevation = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier.fixedNavigationBarsPadding(),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    content = { DragHandle() }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 24.dp,
                            bottom = 16.dp,
                        ),
                ) {
                    Text(
                        text = "Mejiboard",
                        style = MaterialTheme.typography.h6,
                    )

                    Text(
                        text = BuildConfig.VERSION_NAME,
                        style = MaterialTheme.typography.subtitle2,
                        color = MaterialTheme.colors.onSurface.copy(0.7f),
                    )
                }

                Divider(modifier = Modifier.padding(bottom = 8.dp))

                DrawerItem(
                    text = "Home",
                    icon = if (drawerItemSelected == "home") Icons.Filled.Home else Icons.Outlined.Home,
                    onClick = {
                        drawerItemSelected = "home"
                        scope.launch { drawerState.hide() }
                    },
                    selected = drawerItemSelected == "home",
                )

                DrawerItem(
                    text = "Settings",
                    icon = Icons.Outlined.Settings,
                    onClick = {
                        scope.launch {
                            drawerState.hide()
                            mainNavigation.navigate("${MainRoute.Settings}")
                        }
                    },
                    selected = false,
                )

                DrawerItem(
                    text = "About",
                    icon = Icons.Outlined.Info,
                    onClick = {
                        scope.launch {
                            drawerState.hide()
                            mainNavigation.navigate("${MainRoute.About}")
                        }
                    },
                    selected = false,
                )
            }
        },
        content = { },
    )
}