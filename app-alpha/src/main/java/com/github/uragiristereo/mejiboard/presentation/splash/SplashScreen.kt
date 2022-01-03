package com.github.uragiristereo.mejiboard.presentation.splash

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.common.helper.FileHelper
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.google.accompanist.insets.navigationBarsPadding
import kotlinx.coroutines.delay

@ExperimentalAnimationApi
@Composable
fun SplashScreen(
    mainViewModel: MainViewModel,
    backgroundColor: Color,
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(300)
        mainViewModel.checkForUpdate()

        if (mainViewModel.autoCleanCache)
            FileHelper.autoCleanCache(context, 12)
    }

    DisposableEffect(mainViewModel.updateStatus) {
        if (mainViewModel.updateStatus == "update_available" || mainViewModel.updateStatus == "latest" || mainViewModel.updateStatus == "failed") {
            if (mainViewModel.updateStatus == "failed")
                Toast.makeText(context, "Failed to check for update,\nplease check your internet connection", Toast.LENGTH_LONG).show()

            mainViewModel.splashShown = true
        }

        onDispose { }
    }


    Box {
        Box(
            Modifier
                .fillMaxSize()
                .background(backgroundColor),
            contentAlignment = Alignment.Center
        ) {
            Box(
                Modifier.clip(CircleShape)
            ) {
                Image(
                    painterResource(R.drawable.not_like_tsugu),
                    "Logo",
                    Modifier
                        .size(92.dp)
                )
            }
        }

        Box(
            modifier = Modifier
                .navigationBarsPadding()
                .padding(bottom = 48.dp)
                .align(Alignment.BottomCenter)
        ) {
            AnimatedVisibility(
                visible = mainViewModel.updateStatus == "update_required",
                enter = fadeIn(),
                exit = fadeOut(),
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    val latestVersion = mainViewModel.latestVersion.versionName

                    Text(
                        text = "Update required",
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h6,
                        color = MaterialTheme.colors.onSurface,
                    )
                    Text(
                        text = buildAnnotatedString {
                            append("Latest version: ")
                            withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                                append("v$latestVersion")
                            }
                        },
                        modifier = Modifier.padding(bottom = 16.dp),
                        color = MaterialTheme.colors.onSurface,
                    )
                    Button(
                        onClick = {
                            val intent = Intent(
                                Intent.ACTION_VIEW,
                                Uri.parse("https://github.com/uragiristereo/Mejiboard/releases/tag/v$latestVersion")
                            )
                            context.startActivity(intent)
                        }
                    ) { Text("Download latest APK version") }
                }
            }
        }
    }
}