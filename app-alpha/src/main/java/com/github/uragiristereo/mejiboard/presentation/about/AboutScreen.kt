package com.github.uragiristereo.mejiboard.presentation.about

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.drawable.GradientDrawable
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.annotation.ExperimentalCoilApi
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.github.uragiristereo.mejiboard.BuildConfig
import com.github.uragiristereo.mejiboard.R
import com.github.uragiristereo.mejiboard.common.helper.MiuiHelper
import com.github.uragiristereo.mejiboard.presentation.common.theme.MejiboardTheme
import com.github.uragiristereo.mejiboard.presentation.main.MainViewModel
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@ExperimentalCoilApi
@Composable
fun AboutScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel,
) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val systemUiController = rememberSystemUiController()

    val surfaceColor = MaterialTheme.colors.surface

    DisposableEffect(key1 = Unit) {
        if (MiuiHelper.isDeviceMiui() && !mainViewModel.isDesiredThemeDark) {
            systemUiController.setStatusBarColor(Color.Black)
            systemUiController.setNavigationBarColor(surfaceColor)
        } else {
            systemUiController.setStatusBarColor(surfaceColor)
            systemUiController.setNavigationBarColor(surfaceColor.copy(0.4f))
        }

        onDispose { }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "About"
                    )
                },
                backgroundColor = Color.Transparent,
                elevation = 0.dp,
                navigationIcon = {
                    IconButton(
                        onClick = {
                            mainNavigation.navigateUp()
                        }
                    ) {
                        Icon(Icons.Default.ArrowBack, "Back")
                    }
                }
            )
        },
        modifier = Modifier.statusBarsPadding(),
    ) {
        LazyColumn(
            Modifier
                .fillMaxSize()
        ) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.mejiboard_round),
                        contentDescription = "App icon",
                        Modifier
                            .padding(
                                top = 16.dp,
                                bottom = 16.dp
                            )
                            .clip(CircleShape)
                            .size(72.dp)
                    )
                }
            }
            item {
                Text(
                    "Mejiboard",
                    Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 18.sp
                )
            }
            item {
                Text(
                    "Version " + BuildConfig.VERSION_NAME,
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(0.7f)
                )
            }
            item {
                Text(
                    text = buildAnnotatedString {
                        append("An anime image board viewer for Android.\n\n")
                        append("Supported providers: Gelbooru, Danbooru, Safebooru.\n")
                    },
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 8.dp,
                            end = 16.dp,
                            start = 16.dp,
                            bottom = 8.dp
                        ),
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.body2,
                    color = MaterialTheme.colors.onSurface.copy(0.7f)
                )
            }
            item {
                Row(
                    Modifier
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    LinkIconButton(
                        painter = painterResource(if (mainViewModel.isDesiredThemeDark) R.drawable.github_logo_white else R.drawable.github_logo),
                        text = "Github",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/uragiristereo/Mejiboard"))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(
                        Modifier.width(8.dp)
                    )
                    LinkIconButton(
                        painter = painterResource(R.drawable.telegram_logo),
                        text = "Telegram",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/mejiboard_group"))
                            context.startActivity(intent)
                        }
                    )
                    Spacer(
                        Modifier.width(8.dp)
                    )
                }
            }
            item {
                Text(
                    "Developed by",
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            top = 32.dp,
                            end = 16.dp,
                            start = 16.dp,
                            bottom = 12.dp
                        ),
                    style = MaterialTheme.typography.h6
                )
            }
            item {
                Card(
                    Modifier
                        .fillMaxWidth()
                        .padding(
                            start = 16.dp,
                            end = 16.dp
                        ),
                    shape = RoundedCornerShape(8.dp),
                    elevation = 2.dp
                ) {
                    Row(
                        Modifier
                            .padding(16.dp)
                    ) {
                        val placeholder = remember { GradientDrawable() }
                        val size = with(density) { 48.dp.toPx() }.toInt()

                        placeholder.setSize(size, size)
                        placeholder.setColor(android.graphics.Color.DKGRAY)

                        Image(
                            painter = rememberAsyncImagePainter(
                                model = ImageRequest.Builder(context)
                                    .data("https://avatars.githubusercontent.com/u/52477630?v=4")
                                    .placeholder(placeholder)
                                    .crossfade(200)
                                    .build()
                            ),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp),
                            contentScale = ContentScale.Crop
                        )
                        Column(
                            Modifier
                                .padding(start = 16.dp)
                        ) {
                            Text(
                                "Agung Watanabe",
                                fontSize = 18.sp
                            )
                            Text(
                                "@uragiristereo",
                                Modifier
                                    .padding(top = 4.dp),
                                style = MaterialTheme.typography.body2,
                                color = MaterialTheme.colors.onSurface.copy(0.7f)
                            )
                            Row(
                                Modifier
                                    .padding(top = 8.dp)
                            ) {
                                LinkIconButton(
                                    painter = painterResource(if (mainViewModel.isDesiredThemeDark) R.drawable.github_logo_white else R.drawable.github_logo),
                                    text = "Github",
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/uragiristereo"))
                                        context.startActivity(intent)
                                    }
                                )
                                Spacer(
                                    Modifier.width(8.dp)
                                )
                                LinkIconButton(
                                    painter = painterResource(R.drawable.telegram_logo),
                                    text = "Telegram",
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/uragiristereo"))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@ExperimentalCoilApi
@Preview
@Composable
fun AboutPreview() {
    MejiboardTheme {
        AboutScreen(
            rememberNavController(),
            hiltViewModel()
        )
    }
}