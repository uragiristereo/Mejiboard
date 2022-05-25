package com.github.uragiristereo.mejiboard.presentation.settings.bottomsheet

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.presentation.common.DragHandle
import com.github.uragiristereo.mejiboard.presentation.common.mapper.fixedNavigationBarsPadding
import com.github.uragiristereo.mejiboard.presentation.settings.preference.RadioPreferenceItem

@ExperimentalMaterialApi
@Composable
fun SettingsBottomSheet(
    state: ModalBottomSheetState,
    items: List<PreferenceItem>,
    selectedItem: PreferenceItem?,
    onItemSelected: (PreferenceItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalBottomSheetLayout(
        sheetState = state,
        sheetShape = RoundedCornerShape(
            topStart = 12.dp,
            topEnd = 12.dp,
        ),
        scrimColor = Color(color = 0xFF121212).copy(alpha = DrawerDefaults.ScrimOpacity),
        sheetElevation = 0.dp,
        sheetContent = {
            Column(
                modifier = Modifier.fixedNavigationBarsPadding(),
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(all = 8.dp),
                    content = {
                        DragHandle()
                    },
                )

                Text(
                    text = "Select Booru provider",
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier.padding(
                        horizontal = 16.dp,
                        vertical = 8.dp,
                    ),
                )

                items.forEach { item ->
                    RadioPreferenceItem(
                        title = item.title,
                        subtitle = item.subtitle,
                        selected = item == selectedItem,
                        onClick = {
                            onItemSelected(item)
                        },
                    )
                }
            }
        },
        modifier = modifier,
        content = { },
    )
}