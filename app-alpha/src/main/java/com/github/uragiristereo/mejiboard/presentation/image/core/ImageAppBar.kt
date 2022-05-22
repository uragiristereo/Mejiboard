package com.github.uragiristereo.mejiboard.presentation.image.core

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedNavigationBarsPadding
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedStatusBarsPadding
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun ImageAppBar(
    state: ImageState,
    mainNavigation: NavHostController,
    sheetState: ModalBottomSheetState,
    onShowImageChange: (Boolean) -> Unit,
) {
    val scope = rememberCoroutineScope()
    val post = state.selectedPost!!

    AnimatedVisibility(
        visible = state.appBarVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y = when {
                        state.isPressed -> -abs(state.offsetY.roundToInt())
                        else -> -abs(state.animatedOffsetY.roundToInt())
                    }
                )
            }
            .fixedNavigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(
                    insets = WindowInsets.statusBars.add(insets = WindowInsets(top = 56.dp)),
                )
                .background(
                    brush = Brush.verticalGradient(colors = listOf(Color.Black, Color.Transparent)),
                    alpha = 0.5f,
                )
        )

        TopAppBar(
            title = { Text(text = "Post ${post.id}") },
            navigationIcon = {
                IconButton(
                    onClick = { mainNavigation.navigateUp() },
                    content = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = null) },
                )
            },
            actions = {
                if (post.scaled && !state.showOriginalImage && post.originalImage.fileType != "gif") {
                    IconButton(
                        onClick = { onShowImageChange(true) },
                        content = {
                            Icon(
                                painter = painterResource(id = R.drawable.open_in_full),
                                contentDescription = null,
                                tint = Color.White,
                            )
                        }
                    )
                }

                IconButton(
                    onClick = { scope.launch { sheetState.animateTo(ModalBottomSheetValue.Expanded) } },
                    content = {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                )
            },
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            modifier = Modifier.fixedStatusBarsPadding(),
        )
    }
}