package com.github.uragiristereo.mejiboard.data.model.remote.provider

import com.github.uragiristereo.mejiboard.domain.entity.provider.ApiProvider

object ApiProviders {
    val Gelbooru = ApiProvider(
        key = "gelbooru",
        name = "Gelbooru",
        domain = "gelbooru.com",
        webUrlPattern = "https://gelbooru.com/index.php?page=post&s=view&id={postId}",
    )
    val Danbooru = ApiProvider(
        key = "danbooru",
        name = "Danbooru",
        domain = "danbooru.donmai.us",
        webUrlPattern = "https://danbooru.donmai.us/posts/{postId}",
    )
    val SafebooruOrg =  ApiProvider(
        key = "safebooruorg",
        name = "Safebooru.org",
        domain = "safebooru.org",
        webUrlPattern = "https://safebooru.org/index.php?page=post&s=view&id={postId}",
    )

    val list = listOf(
        Gelbooru,
        Danbooru,
        SafebooruOrg,
    )

    val map = list.associate { it.toPair() }
}