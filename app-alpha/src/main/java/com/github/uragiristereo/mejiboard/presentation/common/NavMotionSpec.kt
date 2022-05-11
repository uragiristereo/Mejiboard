package com.github.uragiristereo.mejiboard.presentation.common

import androidx.compose.animation.*
import androidx.compose.animation.core.FastOutLinearInEasing
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import soup.compose.material.motion.EnterMotionSpec
import soup.compose.material.motion.ExitMotionSpec
import soup.compose.material.motion.MotionConstants

private const val ProgressThreshold = 0.35f

private val Int.ForOutgoing: Int
    get() = (this * ProgressThreshold).toInt()

private val Int.ForIncoming: Int
    get() = this - this.ForOutgoing

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

@ExperimentalAnimationApi
fun materialSharedAxisZNoFadeIn(
    durationMillis: Int = MotionConstants.motionDurationLong1,
): EnterMotionSpec = EnterMotionSpec(
    transition = { forward, _ ->
        fadeIn(
            animationSpec = tween(
                durationMillis = durationMillis.ForIncoming,
                delayMillis = durationMillis.ForOutgoing,
                easing = LinearOutSlowInEasing,
            ),
            initialAlpha = if (forward) 0f else 0.4f
        ) +
        scaleIn(
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = FastOutSlowInEasing
            ),
            initialScale = if (forward) 0.8f else 1.1f
        )
    }
)

@ExperimentalAnimationApi
fun materialSharedAxisZNoFadeOut(
    durationMillis: Int = MotionConstants.motionDurationLong1,
): ExitMotionSpec = ExitMotionSpec(
    transition = { forward, _ ->
        fadeOut(
            animationSpec = tween(
                durationMillis = durationMillis.ForOutgoing,
                delayMillis = 0,
                easing = FastOutLinearInEasing
            ),
            targetAlpha = if (forward) 0.8f else 0f
        ) +
        scaleOut(
            animationSpec = tween(
                durationMillis = durationMillis,
                easing = FastOutSlowInEasing
            ),
            targetScale = if (forward) 1.1f else 0.8f
        )
    }
)