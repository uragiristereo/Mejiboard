package com.github.uragiristereo.mejiboard.presentation.common

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import soup.compose.material.motion.EnterMotionSpec
import soup.compose.material.motion.ExitMotionSpec
import soup.compose.material.motion.MotionConstants

@ExperimentalAnimationApi
fun materialFadeIn(
    durationMillis: Int = 250,
): EnterMotionSpec = EnterMotionSpec(
    transition = { _, _ ->
        fadeIn(animationSpec = tween(durationMillis = durationMillis))
    }
)

@ExperimentalAnimationApi
fun materialFadeOut(
    durationMillis: Int = 250,
): ExitMotionSpec = ExitMotionSpec(
    transition = { _, _ ->
        fadeOut(animationSpec = tween(durationMillis = durationMillis))
    }
)

@ExperimentalAnimationApi
fun translateYFadeIn(
    initialOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
    durationMillis: Int = MotionConstants.motionDurationLong1,
): EnterMotionSpec = EnterMotionSpec(
    transition = { _, _ ->
        slideInVertically(
            animationSpec = tween(durationMillis),
            initialOffsetY = initialOffsetY
        ) + fadeIn(
            animationSpec = tween(durationMillis),
        )
    }
)

@ExperimentalAnimationApi
fun translateYFadeOut(
    targetOffsetY: (fullHeight: Int) -> Int = { -it / 2 },
    durationMillis: Int = MotionConstants.motionDurationLong1,
): ExitMotionSpec = ExitMotionSpec(
    transition = { _, _ ->
        slideOutVertically(
            animationSpec = tween(durationMillis),
            targetOffsetY = targetOffsetY
        ) + fadeOut(
            animationSpec = tween(durationMillis),
        )
    }
)