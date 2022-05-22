package com.github.uragiristereo.mejiboard.data.model.remote.provider

sealed class ApiProviders(
    val value: String,
    val name: String,
    val domain: String,
    val baseUrl: String,
    val postsPerPage: Int,
    private val webUrlPattern: String,
) {
    object Gelbooru: ApiProviders(
        value = "gelbooru",
        name = "Gelbooru",
        domain = "gelbooru.com",
        baseUrl = "https://gelbooru.com",
        postsPerPage = 100,
        webUrlPattern = "https://gelbooru.com/index.php?page=post&s=view&id={postId}"
    )

    object GelbooruSafe: ApiProviders(
        value = "gelboorusafe",
        name = "Gelbooru (Safe)",
        domain = "gelbooru.com",
        baseUrl = "https://gelbooru.com",
        postsPerPage = 100,
        webUrlPattern = "https://gelbooru.com/index.php?page=post&s=view&id={postId}"
    )

    override fun toString(): String {
        return "$name ($domain)"
    }

    fun parseWebUrl(postId: Int): String {
        return this.webUrlPattern.replace(oldValue = "{postId}", newValue = postId.toString())
    }
}
