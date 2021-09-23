package com.github.uragiristereo.mejiboard.util

fun getWordInPosition(text: String, position: Int): Triple<String, Int, Int> {
    val delimiters = arrayOf(' ', '{', '}')

    val prevIndexes = IntArray(delimiters.size)
    for (i in delimiters.indices) {
        prevIndexes[i] = text.lastIndexOf(delimiters[i], position - 1)
    }

    val prevDelimiterIndex = prevIndexes.maxOrNull()!!

    val nextIndexes = IntArray(delimiters.size)
    for (i in delimiters.indices) {
        nextIndexes[i] = if (text.indexOf(delimiters[i], position) != -1) text.indexOf(delimiters[i], position) else 99
    }

    val nextDelimiterIndex = if (nextIndexes.minOrNull()!! != 99) nextIndexes.minOrNull()!! else -1

    var startIndex = if (prevDelimiterIndex < 0) 0 else prevDelimiterIndex + 1
    val endIndex = if (nextDelimiterIndex < 0) text.length else nextDelimiterIndex
    if (startIndex >= endIndex) startIndex = 0
    val word = text.substring(startIndex, endIndex)

    return Triple(word, startIndex, endIndex)
}