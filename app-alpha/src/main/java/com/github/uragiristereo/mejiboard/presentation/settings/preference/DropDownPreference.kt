package com.github.uragiristereo.mejiboard.presentation.settings.preference

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun <T> DropDownPreference(
    title: String,
    items: List<T>,
    selectedItem: T,
    onItemSelected: (T) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var dropDownExpanded by remember { mutableStateOf(value = false) }

    RegularPreference(
        title = title,
        subtitle = selectedItem.toString(),
        onClick = {
            dropDownExpanded = true
        },
        modifier = modifier
            .background(
                color = if (dropDownExpanded)
                    MaterialTheme.colors.primary.copy(alpha = 0.2f)
                else
                    Color.Unspecified
            ),
        enabled = enabled,
    )

    Box {
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = { dropDownExpanded = !dropDownExpanded },
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    onClick = {
                        dropDownExpanded = false

                        onItemSelected(item)
                    },
                    modifier = Modifier
                        .background(
                            color = if (selectedItem == item)
                                MaterialTheme.colors.primary.copy(alpha = 0.3f)
                            else
                                Color.Unspecified,
                        ),
                    content = {
                        Text(
                            text = item.toString(),
                            overflow = TextOverflow.Ellipsis,
                        )
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun DropDownPreferencePreview() {
    val themes = listOf("System default", "Light", "Dark")
    var selectedTheme by remember { mutableStateOf(value = themes[0]) }

    DropDownPreference(
        title = "Theme",
        items = themes,
        selectedItem = selectedTheme,
        onItemSelected = {
             selectedTheme = themes[themes.indexOf(it)]
        },
    )
}