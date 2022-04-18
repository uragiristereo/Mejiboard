package com.github.uragiristereo.mejiboard.presentation.splash.common

import android.content.Intent
import android.net.Uri
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.google.accompanist.insets.navigationBarsPadding

@Composable
fun SplashUpdate(
    mainViewModel: MainViewModel,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current

    Box(
        modifier = modifier
            .navigationBarsPadding()
            .padding(bottom = 48.dp),
    ) {
        AnimatedVisibility(
            visible = mainViewModel.updateStatus == "update_required",
            enter = fadeIn(),
            exit = fadeOut(),
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val latestVersion = mainViewModel.latestVersion.versionName

                Text(
                    text = "Update required",
                    modifier = Modifier.padding(bottom = 8.dp),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                )
                Text(
                    text = buildAnnotatedString {
                        append("Latest version: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("v$latestVersion")
                        }
                    },
                    modifier = Modifier.padding(bottom = 16.dp),
                )
                Button(
                    onClick = {
                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/uragiristereo/Mejiboard/releases/tag/v$latestVersion")
                        )

                        context.startActivity(intent)
                    }
                ) {
                    Text(text = "Download latest APK version")
                }
            }
        }
    }
}