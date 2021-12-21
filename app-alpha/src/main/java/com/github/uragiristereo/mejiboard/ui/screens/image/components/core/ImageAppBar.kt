package com.github.uragiristereo.mejiboard.ui.screens.image.components.core

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.model.network.Post
import com.github.uragiristereo.mejiboard.ui.screens.image.ImageViewModel
import com.github.uragiristereo.mejiboard.util.hideSystemBars
import com.github.uragiristereo.mejiboard.util.showSystemBars
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.TopAppBar
import kotlinx.coroutines.launch
import java.io.File

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
        exit = fadeOut()
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(82.dp)
                .background(
                    brush = Brush.verticalGradient(listOf(Color.Black, Color.Transparent)),
                    alpha = 0.5f
                )
        )
        TopAppBar(
            title = { Text("Post ${post.id}") },
            navigationIcon = {
                IconButton(
                    onClick = { mainNavigation.navigateUp() }
                ) {
                    Icon(Icons.Default.ArrowBack, "Back")
                }
            },
            actions = {
                if (post.sample == 1 && !imageViewModel.showOriginalImage && imageType != "gif") {
                    IconButton(
                        onClick = { imageViewModel.showOriginalImage = true }
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.open_in_full),
                            contentDescription = null,
                            tint = Color.White,
                        )
                    }
                }
                IconButton(
                    onClick = {
                        scope.launch { sheetState.animateTo(ModalBottomSheetValue.Expanded) }
                    }
                ) {
                    Icon(Icons.Default.MoreVert, "Back", tint = Color.White)
                }
            },
            elevation = 0.dp,
            contentPadding = rememberInsetsPaddingValues(
                insets = LocalWindowInsets.current.statusBars,
                applyBottom = false,
            ),
            backgroundColor = Color.Transparent,
            contentColor = Color.White,
        )
    }
}