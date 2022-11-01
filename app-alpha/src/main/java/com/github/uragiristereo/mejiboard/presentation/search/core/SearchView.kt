package com.github.uragiristereo.mejiboard.presentation.search.core

import android.view.KeyEvent.ACTION_DOWN
import android.view.KeyEvent.KEYCODE_BACK
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp

@Composable
fun SearchView(
    query: TextFieldValue,
    focusRequester: FocusRequester,
    onQueryTextChange: (TextFieldValue) -> Unit,
    onQueryTextSubmit: (TextFieldValue) -> Unit,
    onBackPressed: () -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    loading: Boolean = false,
) {
    Surface(
        elevation = 2.dp,
        modifier = modifier
            .fillMaxWidth()
            .focusRequester(focusRequester),
    ) {
        TextField(
            value = query,
            onValueChange = onQueryTextChange,
            singleLine = true,
            shape = RectangleShape,
            placeholder = {
                if (placeholder.isNotEmpty())
                    Text(text = placeholder)
            },
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                backgroundColor = Color.Transparent,
            ),
            keyboardOptions = KeyboardOptions(
                autoCorrect = false,
                imeAction = ImeAction.Search,
            ),
            keyboardActions = KeyboardActions(
                onSearch = { onQueryTextSubmit(query) }
            ),
            leadingIcon = {
                IconButton(
                    onClick = onBackPressed,
                    content = { Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "Back") },
                )
            },
            trailingIcon = {
                if (query.text.isNotEmpty())
                    IconButton(
                        onClick = { onQueryTextChange(TextFieldValue(text = "")) },
                        content = {
                            if (loading) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    color = MaterialTheme.colors.secondary,
                                    modifier = Modifier.size(26.dp),
                                )
                            }

                            Icon(imageVector = Icons.Default.Close, contentDescription = "Clear")
                        },
                    )
            },
            modifier = Modifier
                .onPreviewKeyEvent {
                    if (it.nativeKeyEvent.action == ACTION_DOWN && it.nativeKeyEvent.keyCode == KEYCODE_BACK) {
                        onBackPressed()

                        true
                    } else
                        false
                },
        )
    }
}