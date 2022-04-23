package com.github.uragiristereo.mejiboard.presentation.search.quickshortcutbar

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun QuickShortcutBar(
    query: TextFieldValue,
    onQueryChange: (TextFieldValue) -> Unit,
) {
    Card(
        elevation = 2.dp,
        shape = RectangleShape,
    ) {
        LazyRow(modifier = Modifier.fillMaxWidth()) {
            item {
                QuickShortcutItem(
                    text = "CLEAR",
                    onClick = { onQueryChange(TextFieldValue()) },
                )
            }

            item {
                QuickShortcutItem(
                    text = "SPACE",
                    onClick = {
                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = " ",
                                ),
                                selection = TextRange(index = query.selection.start + 1),
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = "-",
                    onClick = {
                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = "-",
                                ),
                                selection = TextRange(index = query.selection.start + 1),
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = ":",
                    onClick = {
                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = ":",
                                ),
                                selection = TextRange(index = query.selection.start + 1),
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = "(",
                    onClick = {
                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = "(",
                                ),
                                selection = TextRange(index = query.selection.start + 1),
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = ")",
                    onClick = {
                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = ")",
                                ),
                                selection = TextRange(index = query.selection.start + 1),
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = "{",
                    onClick = {
                        var replacement = "{"

                        if (query.selection.start > 0)
                            replacement = when {
                                query.text[query.selection.start - 1] == ' ' -> "{"
                                else -> " {"
                            }

                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = replacement,
                                ),
                                selection = TextRange(index = query.selection.start + replacement.length),
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = "}",
                    onClick = {
                        var queryStartIndex = query.selection.start
                        var replacement = "} "

                        if (queryStartIndex < query.text.length)
                            replacement = when {
                                query.text[queryStartIndex] == ' ' -> "}"
                                else -> "} "
                            }

                        if (queryStartIndex > 0)
                            if (query.text[queryStartIndex - 1] == ' ')
                                queryStartIndex -= 1

                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = queryStartIndex,
                                    endIndex = query.selection.end,
                                    replacement = replacement,
                                ),
                                selection = TextRange(index = queryStartIndex + replacement.length)
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = "~",
                    onClick = {
                        var replacement = "~ "

                        if (query.selection.start < query.text.length)
                            replacement = when {
                                query.text[query.selection.start] == ' ' -> "~"
                                else -> "~ "
                            }

                        if (query.selection.start > 0)
                            if (query.text[query.selection.start - 1] != ' ')
                                replacement = " $replacement"

                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = replacement,
                                ),
                                selection = TextRange(index = query.selection.start + replacement.length)
                            )
                        )
                    },
                )
            }

            item {
                QuickShortcutItem(
                    text = "*",
                    onClick = {
                        onQueryChange(
                            TextFieldValue(
                                text = query.text.replaceRange(
                                    startIndex = query.selection.start,
                                    endIndex = query.selection.start,
                                    replacement = "*",
                                ),
                                selection = TextRange(index = query.selection.start + 1)
                            )
                        )
                    },
                )
            }
        }
    }
}