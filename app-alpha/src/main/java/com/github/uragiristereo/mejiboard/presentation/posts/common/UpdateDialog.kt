package com.github.uragiristereo.mejiboard.presentation.posts.common

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel

@Composable
fun UpdateDialog(mainViewModel: MainViewModel) {
    val context = LocalContext.current

    AlertDialog(
        onDismissRequest = {
            mainViewModel.updateDialogVisible = false
        },
        title = { Text("New update available!") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    buildAnnotatedString {
                        append("Current version: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("v${BuildConfig.VERSION_NAME}")
                        }
                    }
                )
                Text(
                    buildAnnotatedString {
                        append("Latest version: ")
                        withStyle(style = SpanStyle(fontWeight = FontWeight.Bold)) {
                            append("v${mainViewModel.latestVersion.versionName}")
                        }
                    }
                )
            }
        },
        buttons = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                horizontalAlignment = Alignment.End
            ) {
                TextButton(
                    onClick = {
                        mainViewModel.updateDialogVisible = false

                        val intent = Intent(
                            Intent.ACTION_VIEW,
                            Uri.parse("https://github.com/uragiristereo/Mejiboard/releases/tag/${mainViewModel.latestVersion.versionName}")
                        )
                        context.startActivity(intent)
                    }
                ) {
                    Text("Download update".uppercase())
                }

                TextButton(
                    onClick = {
                        mainViewModel.apply {
                            updateDialogVisible = false
                            remindLaterCounter = 0
                            updatePreferences { it.copy(remindLaterCounter = 0) }
                        }
                        Toast.makeText(context, "You can check for update manually at:\nSettings > Check for update", Toast.LENGTH_LONG).show()
                    }
                ) {
                    Text(
                        text = "Remind me later".uppercase(),
                        color = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                    )
                }

                TextButton(
                    onClick = {
                        mainViewModel.updateDialogVisible = false
                    }
                ) {
                    Text(
                        text = "Dismiss".uppercase(),
                        color = MaterialTheme.colors.primary.copy(alpha = 0.7f)
                    )
                }
            }
        },
    )
}