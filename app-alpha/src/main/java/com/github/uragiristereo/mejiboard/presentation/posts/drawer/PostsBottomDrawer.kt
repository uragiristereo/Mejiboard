package com.github.uragiristereo.mejiboard.presentation.posts.drawer

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.DrawerDefaults
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.presentation.common.DragHandle
import com.github.uragiristereo.mejiboard.presentation.main.core.MainRoute
import kotlinx.coroutines.launch

@ExperimentalMaterialApi
@Composable
fun PostsBottomDrawer(
    drawerState: ModalBottomSheetState,
    onNavigate: (String) -> Unit,
) {
    val scope = rememberCoroutineScope()

    ModalBottomSheetLayout(
        sheetState = drawerState,
        sheetShape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp),
        scrimColor = Color(0xFF121212).copy(alpha = DrawerDefaults.ScrimOpacity),
        sheetElevation = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier.navigationBarsPadding(),
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

                val hideDrawer: () -> Unit = remember {
                    {
                        scope.launch {
                            drawerState.hide()
                        }
                    }
                }

                val hideDrawerAndNavigate: (String) -> Unit = remember {
                    {route ->
                        scope.launch {
                            drawerState.hide()
                            onNavigate(route)
                        }
                    }
                }

                DrawerItem(
                    text = "Home",
                    icon = Icons.Filled.Home,
                    onClick = hideDrawer,
                    selected = true,
                )

                DrawerItem(
                    text = "Settings",
                    icon = Icons.Outlined.Settings,
                    onClick = {
                        hideDrawerAndNavigate("${MainRoute.Settings}")
                    },
                    selected = false,
                )

                DrawerItem(
                    text = "About",
                    icon = Icons.Outlined.Info,
                    onClick = {
                        hideDrawerAndNavigate("${MainRoute.About}")
                    },
                    selected = false,
                )
            }
        },
        content = { },
    )
}