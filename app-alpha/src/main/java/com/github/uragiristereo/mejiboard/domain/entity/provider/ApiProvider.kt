package com.github.uragiristereo.mejiboard.domain.entity.provider

data class ApiProvider(
    val key: String,
    val name: String,
    val domain: String,
    private val webUrlPattern: String,
) {
    fun baseUrl(): String {
        return "https://${this.domain}"
    }

    fun parseWebUrl(postId: Int): String {
        return this.webUrlPattern
            .replace(
                oldValue = "{postId}",
                newValue = postId.toString(),
            )
    }

    fun toPair(): Pair<String, ApiProvider> {
        return this.key to this
    }
}
