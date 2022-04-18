package com.github.uragiristereo.mejiboard.data.preferences.enums

import kotlinx.serialization.Serializable

@Serializable
sealed class DohProvider(
    val name: String,
    val url: String,
) {
    @Serializable
    object Cloudflare : DohProvider(name = "cloudflare", url = "https://cloudflare-dns.com/dns-query")

    @Serializable
    object Google : DohProvider(name = "google", url = "https://dns.google/dns-query")

    @Serializable
    object Tuna : DohProvider(name = "tuna", url = "https://101.6.6.6:8443/dns-query")

    override fun toString(): String {
        return this.name.replaceFirstChar { it.uppercase() }
    }
}