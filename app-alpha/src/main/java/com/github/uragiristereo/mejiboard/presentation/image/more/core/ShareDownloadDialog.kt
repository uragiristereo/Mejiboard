package com.github.uragiristereo.mejiboard.presentation.image.more.core

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.presentation.common.mapper.update
import com.github.uragiristereo.mejiboard.presentation.image.more.MoreViewModel
import com.github.uragiristereo.mejiboard.presentation.main.LocalMainViewModel

@Composable
fun ShareDownloadDialog(
    state: MoreState,
    viewModel: MoreViewModel,
) {
    val mainViewModel = LocalMainViewModel.current
    val post = state.selectedPost!!

    AlertDialog(
        onDismissRequest = { },
        buttons = {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        end = 16.dp,
                        bottom = 8.dp
                    ),
                contentAlignment = Alignment.CenterEnd
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(size = 4.dp))
                        .clickable {
                            viewModel.state.update { it.copy(dialogShown = false) }

                            val instance = mainViewModel.getInstance(postId = post.id)
                            instance?.cancel()

                            mainViewModel.removeInstance(post.id)
                        }
                        .padding(all = 8.dp),
                ) {
                    Text(
                        text = "Cancel".uppercase(),
                        color = MaterialTheme.colors.primary,
                    )
                }
            }
        },
        title = { Text(text = "Downloading...") },
        text = {
            val progressSmooth by animateFloatAsState(targetValue = state.shareDownloadInfo.progress)

            Column {
                val progressFormatted = "%.2f".format(state.shareDownloadInfo.progress.times(100))

                if (progressSmooth != 0f) {
                    LinearProgressIndicator(
                        progress = progressSmooth,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                    )
                } else {
                    LinearProgressIndicator(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 4.dp),
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(text = "$progressFormatted%")

                    Text(text = "${viewModel.convertFileSize(state.shareDownloadSpeed)}/s")
                }

                Text(
                    text = "${viewModel.convertFileSize(state.shareDownloadInfo.downloaded)} / ${viewModel.convertFileSize(state.shareDownloadInfo.length)}",
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.End,
                )
            }
        }
    )
}