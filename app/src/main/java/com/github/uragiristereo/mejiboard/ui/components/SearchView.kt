package com.github.uragiristereo.mejiboard.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    query: MutableState<TextFieldValue>,
    placeholder: String,
    onQueryTextChange: (TextFieldValue) -> Unit,
    onBackPressed: () -> Unit,
    onQueryTextSubmit: (TextFieldValue) -> Unit
) {
    var canLostFocus by remember { mutableStateOf(false) }
    var searchQuery by query

    Surface(
        modifier = modifier.fillMaxWidth(),
        elevation = 8.dp
    ) {
        TextField(
            modifier = Modifier
                .onFocusChanged {
                    if (!it.isFocused && canLostFocus) onBackPressed()
                    canLostFocus = true
                },
            value = searchQuery,
            onValueChange = {
                onQueryTextChange(it)
                searchQuery = it
            },
            placeholder = { Text(placeholder) },
            singleLine = true,
            leadingIcon = {
                IconButton(
                    onClick = onBackPressed
                ) {
                    Icon(
                        Icons.Default.ArrowBack,
                        contentDescription = ""
                    )
                }
            },
            trailingIcon = {
                if (searchQuery.text != "") {
                    IconButton(
                        onClick = {
                            searchQuery = TextFieldValue("")
                            onQueryTextChange(searchQuery)
                        }
                    ) {
                        Icon(
                            Icons.Default.Close,
                            contentDescription = ""
                        )
                    }
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                canLostFocus = false
                onQueryTextSubmit(searchQuery)
            }),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                backgroundColor = MaterialTheme.colors.background
            ),
            shape = RectangleShape
        )
    }
}