package com.uragiristereo.mejiboard.ui.screens.about

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.request.ImageRequest
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.rememberInsetsPaddingValues
import com.google.accompanist.insets.ui.Scaffold
import com.google.accompanist.insets.ui.TopAppBar
import com.skydoves.landscapist.coil.CoilImage
import com.uragiristereo.mejiboard.BuildConfig
import com.uragiristereo.mejiboard.R
import com.uragiristereo.mejiboard.ui.components.LinkIconButton
import com.uragiristereo.mejiboard.ui.theme.MejiboardTheme
import com.uragiristereo.mejiboard.ui.viewmodel.MainViewModel

@Composable
fun AboutScreen(
    mainNavigation: NavHostController,
    mainViewModel: MainViewModel
) {
    val context = LocalContext.current

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
                contentPadding = rememberInsetsPaddingValues(
                    LocalWindowInsets.current.statusBars,
                    applyBottom = false,
                ),
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
        }
    ) { innerPadding ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            item {
                Box(
                    Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.not_like_tsugu),
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
                    "An image board client based on Gelbooru for Android.",
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
                        painter = painterResource(R.drawable.gelbooru_logo),
                        text = "Gelbooru",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://gelbooru.com"))
                            context.startActivity(intent)
                        }
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
                    elevation = 4.dp
                ) {
                    Row(
                        Modifier
                            .padding(16.dp)
                    ) {
                        CoilImage(
                            imageRequest = ImageRequest
                                .Builder(context)
                                .data("https://avatars.githubusercontent.com/u/52477630?v=4")
                                .crossfade(true)
                                .build(),
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(48.dp),
                            imageLoader = mainViewModel.imageLoader,
                            loading = {
                                Box(
                                    Modifier
                                        .fillMaxSize()
                                        .background(Color.DarkGray)
                                )
                            }
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
                                    painter = painterResource(R.drawable.facebook_logo),
                                    text = "Facebook",
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://facebook.com/agutenx"))
                                        context.startActivity(intent)
                                    }
                                )
                            }
                            Row(
                                Modifier
                                    .padding(top = 8.dp)
                            ) {
                                LinkIconButton(
                                    painter = painterResource(R.drawable.twitter_logo),
                                    text = "Twitter",
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/uragiristereo"))
                                        context.startActivity(intent)
                                    }
                                )
                                Spacer(
                                    Modifier.width(8.dp)
                                )
                                LinkIconButton(
                                    painter = painterResource(R.drawable.outlook_logo),
                                    text = "Email",
                                    onClick = {
                                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse("mailto:raava@outlook.co.id"))
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