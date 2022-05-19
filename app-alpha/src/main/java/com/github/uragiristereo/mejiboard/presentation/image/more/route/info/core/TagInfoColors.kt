package com.github.uragiristereo.mejiboard.presentation.image.more.route.info.core

import androidx.compose.ui.graphics.Color

object TagColors {
    val colors = mapOf(
        TermType.Artist to TagColor(
            lightColor = Color(color = 0xFFDA100B),
            lightBackgroundColor = Color(color = 0xFFFCD0CF),
            darkColor = Color(color = 0xFFFCD0CF),
//            darkBackgroundColor = Color(color = 0xFFF7716E),
            darkBackgroundColor = Color(color = 0xFFFAA09E),
        ),
        TermType.Character to TagColor(
            lightColor = Color(color = 0xFF4D085F),
            lightBackgroundColor = Color(color = 0xFFECB9F9),
            darkColor = Color(color = 0xFFECB9F9),
//            darkBackgroundColor = Color(color = 0xFFC52CEE),
            darkBackgroundColor = Color(color = 0xFFD972F4),
        ),
        TermType.Copyright to TagColor(
            lightColor = Color(color = 0xFF155D18),
            lightBackgroundColor = Color(color = 0xFFC5F2C7),
            darkColor = Color(color = 0xFFC5F2C7),
//            darkBackgroundColor = Color(color = 0xFF52D858),
            darkBackgroundColor = Color(color = 0xFF8CE590),
        ),
        TermType.Metadata to TagColor(
            lightColor = Color(color = 0xFF8E750B),
            lightBackgroundColor = Color(color = 0xFFFCD0CF),
            darkColor = Color(color = 0xFFFCD0CF),
            darkBackgroundColor = Color(color = 0xFFF4DB71),
//            darkBackgroundColor = Color(color = 0xFFF7E7A1),
        ),
        TermType.Tag to TagColor(
            lightColor = Color(color = 0xFF0A3977),
            lightBackgroundColor = Color(color = 0xFFC5DCFA),
            darkColor = Color(color = 0xFFC5DCFA),
            darkBackgroundColor = Color(color = 0xFF5096F1),
//            darkBackgroundColor = Color(color = 0xFF8AB9F6),
        ),
    )
}

data class TagColor(
    val lightColor: Color,
    val lightBackgroundColor: Color,
    val darkColor: Color,
    val darkBackgroundColor: Color,
)