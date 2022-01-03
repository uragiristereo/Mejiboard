package com.github.uragiristereo.mejiboard.presentation.common

import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.github.uragiristereo.mejiboard.presentation.theme.MejiboardTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SettingsCategory(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text,
        modifier
            .padding(start = 16.dp, top = 24.dp, bottom = 8.dp, end = 16.dp),
        color = MaterialTheme.colors.primary,
        style = MaterialTheme.typography.subtitle2
    )
}

@Composable
fun SettingsItem(
    title: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    enabled: Boolean = true,
    action: @Composable () -> Unit = {}
) {
    SettingsItem(
        title = title,
        subtitle = "",
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        enabled = enabled,
        action = action
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    enabled: Boolean = true,
    action: @Composable () -> Unit = {}
) {
    SettingsItem(
        title = title,
        subtitle = AnnotatedString(subtitle),
        onClick = onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        enabled = enabled,
        action = action
    )
}

@Composable
fun SettingsItem(
    title: String,
    subtitle: AnnotatedString,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = MutableInteractionSource(),
    enabled: Boolean = true,
    action: @Composable () -> Unit = {}
) {
    val context = LocalContext.current.resources
    val displayMetrics = context.displayMetrics
    val screenWidth = displayMetrics.widthPixels / displayMetrics.density

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                interactionSource = interactionSource,
                indication = LocalIndication.current,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        Row(
            Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    title,
                    Modifier.width(screenWidth.times(0.8f).dp),
                    style = MaterialTheme.typography.body1,
                    color = if (enabled) Color.Unspecified else MaterialTheme.colors.onSurface.copy(
                        ContentAlpha.disabled
                    )
                )
                if (subtitle.isNotEmpty())
                    Text(
                        subtitle,
                        Modifier.width(screenWidth.times(0.8f).dp),
                        style = MaterialTheme.typography.body2,
                        color = if (enabled) MaterialTheme.colors.onSurface.copy(0.5f) else MaterialTheme.colors.onSurface.copy(
                            ContentAlpha.disabled
                        )
                    )
            }
            action()
        }
    }
}

data class SettingsOptionsItem(
    val key: String,
    val value: String
)

@Composable
fun SettingsOptions(
    title: String,
    subtitle: String,
    items: List<SettingsOptionsItem>,
    selectedItemKey: String,
    onItemSelected: (SettingsOptionsItem) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    var dropDownExpanded by remember { mutableStateOf(false) }
//    var offsetY by remember { mutableStateOf(0.dp) }
    var itemOffsetY by remember { mutableStateOf(0.dp) }
    val selectedItem = items.filter { it.key == selectedItemKey }[0]
    var selectedItemIndexState by remember { mutableStateOf(items.indexOf(selectedItem)) }
    var dropDownItemOffsetY by remember { mutableStateOf(((-48).dp * selectedItemIndexState.plus(1)) - 14.dp) }
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    dropDownItemOffsetY = ((-48).dp * selectedItemIndexState.plus(1)) - 14.dp

    SettingsItem(
        title = title,
        subtitle = subtitle,
        enabled = enabled,
        modifier = modifier
            .onGloballyPositioned {
//                offsetY = with(density) { it.positionInWindow().y.toDp() }
//                Timber.i("chooseThemeOffsetY = $offsetY")
            },
        onClick = {
            dropDownExpanded = !dropDownExpanded
        }
    )
    Box(
        Modifier
            .onGloballyPositioned {
                itemOffsetY = with(density) { it.positionInWindow().y.toDp() }
//                Timber.i("itemOffsetY = $itemOffsetY")
            }
            .offset(
                x = 16.dp,
//                            y = (-96).minus(28).dp
//                            y = if (((-48).dp * selectedItemIndex.plus(1)) - 14.dp > -itemOffsetY.minus(48.dp)) ((-48).dp * selectedItemIndex.plus(1)) - 14.dp else (-48).dp - 14.dp
                y = if (dropDownItemOffsetY > -itemOffsetY.minus(48.dp)) dropDownItemOffsetY else (-48).dp - 14.dp
//                            y = if (itemOffsetY.minus(124.dp) >= 52.dp) (-96).minus(28).dp else 0.dp
            )
    ) {
        DropdownMenu(
            expanded = dropDownExpanded,
            onDismissRequest = {
                dropDownExpanded = false
            },
        ) {
            items.forEachIndexed { index, item ->
                DropdownMenuItem(
                    onClick = {
                        dropDownExpanded = false
                        scope.launch {
                            delay(100)
                            selectedItemIndexState = index
                            onItemSelected(item)
                        }
                    },
                    Modifier.background(if (selectedItemIndexState == index) MaterialTheme.colors.primary.copy(alpha = 0.3f) else Color.Unspecified)
                ) {
                    Text(item.value)
                }
            }
        }
    }
}

@Preview
@Composable
fun Prev() {
    MejiboardTheme(
        theme = "dark",
        blackTheme = true
    ) {
        var state by remember { mutableStateOf(false) }
        val interactionSource = remember { MutableInteractionSource() }

        SettingsItem(
            title = "Dark theme",
            subtitle = "Use pitch black theme instead of regular dark theme, useful for AMOLED user",
            onClick = {
                state = !state
            },
            interactionSource = interactionSource,
        ) {
            Switch(checked = state, onCheckedChange = { state = !state }, interactionSource = interactionSource)
        }
    }
}