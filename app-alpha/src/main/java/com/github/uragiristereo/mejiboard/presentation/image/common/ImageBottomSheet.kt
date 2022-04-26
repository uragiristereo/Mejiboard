package com.github.uragiristereo.mejiboard.presentation.image.common

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.common.helper.ImageHelper
import com.github.uragiristereo.mejiboard.domain.entity.Post
import com.github.uragiristereo.mejiboard.presentation.common.ThumbPill
import com.github.uragiristereo.mejiboard.presentation.image.ImageViewModel
import com.github.uragiristereo.mejiboard.presentation.image.more.PostMoreNavigation
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import kotlinx.coroutines.launch
import soup.compose.material.motion.navigation.rememberMaterialMotionNavController
import java.io.File

@ExperimentalAnimationApi
@ExperimentalMaterialApi
@Composable
fun ImageBottomSheet(
    mainViewModel: MainViewModel,
    imageViewModel: ImageViewModel,
    post: Post,
    sheetState: ModalBottomSheetState,
) {
    val moreNavigation = rememberMaterialMotionNavController()
    val imageType = remember { File(post.image).extension }

    LaunchedEffect(
        key1 = sheetState.isVisible,
        key2 = imageViewModel.shareModalVisible,
    ) {
        if (!sheetState.isVisible && imageViewModel.shareModalVisible) {
            moreNavigation.navigateUp()
            imageViewModel.shareModalVisible = false
        }
    }

    LaunchedEffect(key1 = sheetState.currentValue) {
        if (sheetState.currentValue == ModalBottomSheetValue.HalfExpanded && sheetState.isVisible) {
            launch {
                imageViewModel.showTagsIsCollapsed = true
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
        scrimColor = Color(color = 0xFF121212)
            .copy(alpha = DrawerDefaults.ScrimOpacity),
        sheetContent = {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colors.background)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    ThumbPill()
                }

                PostMoreNavigation(
                    imageViewModel,
                    sheetState,
                    post,
                    ImageHelper.parseImageUrl(
                        post = post,
                        original = false
                    ),
                    ImageHelper.parseImageUrl(
                        post = post,
                        original = true
                    ),
                    imageType,
                    moreNavigation,
                    mainViewModel
                )

                Box(
                    modifier = Modifier
                        .navigationBarsPadding()
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { }
}