package com.github.uragiristereo.mejiboard.presentation.common.mapper

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.github.uragiristereo.mejiboard.presentation.main.LocalFixedInsets

@Composable
fun Modifier.fixedStatusBarsPadding(): Modifier {
    return this.padding(top = LocalFixedInsets.current.statusBarHeight)
}

@Composable
fun Modifier.fixedNavigationBarsPadding(): Modifier {
    return this.padding(paddingValues = LocalFixedInsets.current.navigationBarsPadding)
}