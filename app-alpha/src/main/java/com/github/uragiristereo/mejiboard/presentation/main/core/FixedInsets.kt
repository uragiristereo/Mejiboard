package com.github.uragiristereo.mejiboard.presentation.main.core

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class FixedInsets(
    val statusBarHeight: Dp = 0.dp,
    val navigationBarsPadding: PaddingValues = PaddingValues(),
)
