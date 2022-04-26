package com.github.uragiristereo.mejiboard.presentation.image.common

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.common.extension.hideSystemBars
import com.github.uragiristereo.mejiboard.common.extension.showSystemBars
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import kotlinx.coroutines.launch
import java.io.File
import kotlin.math.abs
import kotlin.math.roundToInt

@ExperimentalMaterialApi
@Composable
fun ImageAppBar(
    imageViewModel: ImageViewModel,
    post: Post,
    appBarVisible: MutableState<Boolean>,
    mainNavigation: NavHostController,
    sheetState: ModalBottomSheetState,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val window = (context as Activity).window
    val imageType = remember { File(post.image).extension }

    LaunchedEffect(key1 = appBarVisible.value) {
        if (appBarVisible.value)
            window.showSystemBars()
        else
            window.hideSystemBars()
    }

    AnimatedVisibility(
        visible = appBarVisible.value,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = Modifier
            .offset {
                IntOffset(
                    x = 0,
                    y =
                    if (imageViewModel.isPressed)
                        -abs(imageViewModel.offsetY.roundToInt())
                    else
                        -abs(imageViewModel.animatedOffsetY.roundToInt()),
                )
            }
            .navigationBarsPadding(),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsTopHeight(
                    insets = WindowInsets.statusBars.add(insets = WindowInsets(top = 56.dp)),
                )
                .background(
                    brush = Brush.verticalGradient(colors = listOf(Color.Black, Color.Transparent)),
                    alpha = 0.5f
                )
        )

        TopAppBar(
            title = { Text(text = "Post ${post.id}") },
            navigationIcon = {
                IconButton(
                    onClick = { mainNavigation.navigateUp() },
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = null,
                    )
                }
            },
            actions = {
                if (post.sample == 1 && !imageViewModel.showOriginalImage && imageType != "gif") {
                    IconButton(
                        onClick = { imageViewModel.showOriginalImage = true },
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.open_in_full),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
                IconButton(
                    onClick = { scope.launch { sheetState.animateTo(ModalBottomSheetValue.Expanded) } },
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Back",
                        tint = Color.White,
                    )
                }
            },
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
            modifier = Modifier.statusBarsPadding(),
        )
    }
}