package com.github.uragiristereo.mejiboard.presentation.image.more

import android.content.res.Configuration
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.github.uragiristereo.mejiboard.presentation.common.DragHandle
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedNavigationBarsPadding
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.route.MoreNavGraph
import com.github.uragiristereo.mejiboard.presentation.main.LocalFixedInsets
import kotlinx.coroutines.launch

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun MoreBottomSheet(
    sheetState: ModalBottomSheetState,
    viewModel: ImageViewModel = hiltViewModel(),
) {
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val fixedInsets = LocalFixedInsets.current
    val density = LocalDensity.current
    val screenWidth = LocalConfiguration.current.screenWidthDp.dp

    var sheetHeight by remember { mutableStateOf(0.dp) }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.HalfExpanded && sheetState.isVisible) {
            launch {
                sheetState.animateTo(ModalBottomSheetValue.Expanded)
            }
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
        ),
        scrimColor = Color(color = 0xFF121212).copy(alpha = DrawerDefaults.ScrimOpacity),
        sheetBackgroundColor = Color.Transparent,
        sheetElevation = 0.dp,
        sheetContent = {
            Row {
                Box(
                    modifier = Modifier
                        .let {
                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> it
                                    .pointerInput(key1 = Unit) {
                                        detectTapGestures {
                                            scope.launch {
                                                sheetState.animateTo(ModalBottomSheetValue.Hidden)
                                            }
                                        }
                                    }
                                    .size(
                                        width = screenWidth * 0.4f,
                                        height = sheetHeight,
                                    )
                                else -> it
                            }
                        },
                )

                Column(
                    modifier = Modifier
                        .weight(weight = 1f, fill = true)
                        .let {
                            when (configuration.orientation) {
                                Configuration.ORIENTATION_LANDSCAPE -> it.clip(RoundedCornerShape(topStart = 12.dp))
                                else -> it
                            }
                        }
                        .background(color = MaterialTheme.colors.background)
                        .onGloballyPositioned {
                            sheetHeight = with(density) { it.size.height.toDp() }
                        },
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(all = 8.dp),
                        content = { DragHandle() },
                    )

                    MoreNavGraph(
                        sheetState = sheetState,
                        imageViewModel = viewModel,
                    )

                    Spacer(modifier = Modifier.fixedNavigationBarsPadding())
                }
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .padding(
                top = fixedInsets.statusBarHeight,
                end = fixedInsets.navigationBarsPadding.calculateEndPadding(LayoutDirection.Ltr),
            ),
        content = { },
    )
}