package com.github.uragiristereo.mejiboard.common

import com.github.uragiristereo.mejiboard.domain.entity.provider.post.Rating

object RatingFilter {
    // this lists ratings that to be FILTERED
    val GENERAL_ONLY = listOf(Rating.SENSITIVE, Rating.QUESTIONABLE, Rating.EXPLICIT)
    val SAFE = listOf(Rating.QUESTIONABLE, Rating.EXPLICIT)
    val NO_EXPLICIT = listOf(Rating.EXPLICIT)
    val UNFILTERED = listOf<Rating>()
}