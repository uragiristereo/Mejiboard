package com.github.uragiristereo.mejiboard.data.preferences.enums


object DohProvider {
    const val Cloudflare = "cloudflare"
    const val Google = "google"
    const val Tuna = "tuna"

    fun getUrl(provider: String): String {
        return when (provider) {
            Cloudflare -> "https://cloudflare-dns.com/dns-query"
            Google -> "https://dns.google/dns-query"
            else -> "https://101.6.6.6:8443/dns-query"
        }
    }
}