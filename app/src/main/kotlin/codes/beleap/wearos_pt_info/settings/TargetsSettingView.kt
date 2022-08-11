package codes.beleap.wearos_pt_info.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.PlusOne
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import kotlinx.coroutines.launch

@Composable
fun TargetsSettingView(
    settingsRepository: SettingsRepository,
) {
    val listState = rememberScalingLazyListState()
    val vignettePosition = remember { mutableStateOf(VignettePosition.TopAndBottom) }

    Scaffold(
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState,
                modifier = Modifier,
            )
        },
        vignette = { Vignette(vignettePosition = vignettePosition.value) },
        timeText = {
            TimeText()
        }
    ) {
        val scope = rememberCoroutineScope()

        val targets: MutableState<List<String>> = remember { mutableStateOf(listOf()) }
        LaunchedEffect(key1 = Unit) {
            targets.value = settingsRepository.getSettings().targets.toMutableList()
        }

        val focusManager = LocalFocusManager.current

        val itemSpacing = 5.dp
        val scrollOffset = with(LocalDensity.current) {
            -(itemSpacing / 2).roundToPx()
        }

        ScalingLazyColumn(
            contentPadding = PaddingValues(top = 25.dp),
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            autoCentering = AutoCenteringParams(itemOffset = scrollOffset),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            items(targets.value.size) { idx ->
                val value = targets.value[idx]

                BasicTextField(
                    value = value,
                    onValueChange = { newValue ->
                        val mutableTargets = targets.value.toMutableList()
                        mutableTargets[idx] = newValue
                        targets.value = mutableTargets
                    },
                    modifier = Modifier
                        .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(16.dp))
                        .fillMaxSize(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = {
                            scope.launch {
                                settingsRepository.updateTargets(targets.value)
                            }
                            focusManager.clearFocus()
                        }
                    ),
                    singleLine = true,
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            value,
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .padding(
                                    horizontal = 25.dp,
                                ),
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        CompactButton(
                            onClick = {
                                val mutableTargets = targets.value.toMutableList()
                                mutableTargets.removeAt(idx)

                                targets.value = mutableTargets
                                scope.launch {
                                    settingsRepository.updateTargets(targets.value)
                                }
                            },
                            colors = ButtonDefaults.secondaryButtonColors()
                        ) {
                            Icon(Icons.Rounded.Remove, "Remove $idx")
                        }
                    }
                }
            }

            item {
                CompactButton(
                    onClick = {
                        targets.value = targets.value + listOf("")
                        scope.launch {
                            settingsRepository.updateTargets(targets.value)
                        }
                    },
                    modifier = Modifier.fillMaxSize(),
                    colors = ButtonDefaults.secondaryButtonColors()
                ) {
                    Icon(Icons.Rounded.Add, "Add")
                }
            }
        }
    }
}