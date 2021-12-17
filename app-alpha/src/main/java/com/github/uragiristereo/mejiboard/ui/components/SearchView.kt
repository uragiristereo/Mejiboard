package com.github.uragiristereo.mejiboard.ui.components

import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_BACK
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SearchView(
    modifier: Modifier = Modifier,
    query: TextFieldValue,
    placeholder: String,
    onQueryTextChange: (TextFieldValue) -> Unit,
    onBackPressed: () -> Unit,
    onQueryTextSubmit: (TextFieldValue) -> Unit,
    backgroundColor: Color = MaterialTheme.colors.background
) {
    onQueryTextChange(query)

    Surface(
        modifier = modifier.fillMaxWidth(),
        elevation = 2.dp
    ) {
        TextField(
            modifier = Modifier
                .onPreviewKeyEvent {
                    if (it.nativeKeyEvent.action == ACTION_DOWN && it.nativeKeyEvent.keyCode == KEYCODE_BACK) {
                        onBackPressed()
                        true
                    } else
                        false
                },
            value = query,
            onValueChange = {
                onQueryTextChange(it)
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
                if (query.text != "") {
                    IconButton(
                        onClick = {
                            onQueryTextChange(TextFieldValue(""))
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
            keyboardActions = KeyboardActions(
                onSearch = { onQueryTextSubmit(query) }
            ),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent
            ),
            shape = RectangleShape
        )
    }
}