package com.github.uragiristereo.mejiboard.presentation.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.github.uragiristereo.mejiboard.data.preferences.enums.Theme
import com.google.accompanist.systemuicontroller.rememberSystemUiController

private val DarkColorPalette = darkColors(
    primary = DeepPurple200,
    primaryVariant = DeepPurple700,
    secondaryVariant = DeepPurple700,
    secondary = DeepPurple200
)

private val BlackColorPalette = darkColors(
    primary = DeepPurple200,
    primaryVariant = DeepPurple700,
    secondary = DeepPurple200,
    secondaryVariant = DeepPurple700,
    background = Color.Black,
    surface = Color.Black
)

private val LightColorPalette = lightColors(
    primary = DeepPurple500,
    primaryVariant = DeepPurple700,
    secondary = DeepPurple500,
    secondaryVariant = DeepPurple700,

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)

@Composable
fun MejiboardTheme(
    theme: Theme = Theme.System,
    blackTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors =
        when (theme) {
            Theme.Light -> LightColorPalette
            Theme.Dark -> if (blackTheme) BlackColorPalette else DarkColorPalette
            Theme.System -> {
                if (isSystemInDarkTheme())
                    if (blackTheme)
                        BlackColorPalette
                    else
                        DarkColorPalette
                else
                    LightColorPalette
            }
        }

    rememberSystemUiController().setStatusBarColor(color = colors.surface)

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}