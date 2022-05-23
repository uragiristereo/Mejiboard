package com.github.uragiristereo.mejiboard.data.model.remote.provider

sealed class ApiProviders(
    val value: String,
    val domain: String,
    val baseUrl: String,
    val postsPerPage: Int,
    private val name: String,
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

    object SafebooruOrg: ApiProviders(
        value = "safebooruorg",
        name = "Safebooru",
        domain = "safebooru.org",
        baseUrl = "https://safebooru.org",
        postsPerPage = 100,
        webUrlPattern = "https://safebooru.org/index.php?page=post&s=view&id={postId}"
    )

    object Danbooru: ApiProviders(
        value = "danbooru",
        name = "Danbooru",
        domain = "danbooru.donmai.us",
        baseUrl = "https://danbooru.donmai.us",
        postsPerPage = 100,
        webUrlPattern = "https://danbooru.donmai.us/posts/{postId}"
    )

    override fun toString(): String {
        return "$name ($domain)"
    }

    fun parseWebUrl(postId: Int): String {
        return this.webUrlPattern.replace(oldValue = "{postId}", newValue = postId.toString())
    }

    fun toPair(): Pair<String, String> {
        return this.value to this.toString()
    }
}
