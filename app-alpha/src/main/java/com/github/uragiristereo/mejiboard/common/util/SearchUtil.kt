package com.github.uragiristereo.mejiboard.common.util

object SearchUtil {
    fun parseSearchQuery(query: String): String {
        var submitQuery = query
            .replace("\\s+".toRegex(), " ")
            .replace("{ ", "{")
            .replace(" }", "}")

        if (submitQuery.isNotEmpty()) {
            if (submitQuery[submitQuery.length - 1] != ' ')
                submitQuery = "$submitQuery "
        }

        if (submitQuery == " ") {
            submitQuery = ""
        }

        return submitQuery.lowercase()
    }

    fun getWordInPosition(text: String, position: Int): Triple<String, Int, Int> {
        val delimiters = arrayOf(' ', '{', '}')

        val prevIndexes = IntArray(delimiters.size)
        val nextIndexes = IntArray(delimiters.size)

        delimiters.forEachIndexed { index, delimiter ->
            prevIndexes[index] = text.lastIndexOf(char = delimiter, startIndex = position - 1)
        }

        delimiters.forEachIndexed { index, delimiter ->
            nextIndexes[index] = if (text.indexOf(char = delimiter, startIndex = position) != -1)
                text.indexOf(delimiter, position)
            else
                text.length
        }

        val prevDelimiterIndex = prevIndexes.maxOrNull()!!
        val nextDelimiterIndex =
            if (nextIndexes.minOrNull()!! != text.length)
                nextIndexes.minOrNull()!!
            else
                -1
        var startIndex =
            if (prevDelimiterIndex < 0)
                0
            else
                prevDelimiterIndex + 1
        val endIndex =
            if (nextDelimiterIndex < 0)
                text.length
            else
                nextDelimiterIndex

        if (startIndex >= endIndex) {
            startIndex = 0
        }

        val word = text.substring(startIndex = startIndex, endIndex = endIndex)

        return Triple(first = word, second = startIndex, third = endIndex)
    }
}