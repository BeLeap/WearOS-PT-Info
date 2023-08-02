package codes.beleap.wearos_pt_info.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberScalingLazyListState
import kotlinx.coroutines.launch

@Composable
fun TargetsSettingView(
    settingsRepository: SettingsRepository,
) {
    val listState = rememberScalingLazyListState()
    val vignettePosition by remember { mutableStateOf(VignettePosition.TopAndBottom) }

    Scaffold(
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState,
                modifier = Modifier,
            )
        },
        vignette = { Vignette(vignettePosition = vignettePosition) },
        timeText = {
            TimeText()
        }
    ) {
        val scope = rememberCoroutineScope()

        var targets: List<Target> by remember { mutableStateOf(listOf()) }
        LaunchedEffect(key1 = Unit) {
            targets = settingsRepository.getSettings().targets.toMutableList()
        }

        val focusManager = LocalFocusManager.current
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }
        val focusScope = rememberCoroutineScope()

        val itemSpacing = 5.dp
        val scrollOffset = with(LocalDensity.current) {
            -(itemSpacing / 2).roundToPx()
        }

        ScalingLazyColumn(
            contentPadding = PaddingValues(top = 25.dp),
            state = listState,
            modifier = Modifier
                .onRotaryScrollEvent {
                    focusScope.launch {
                        listState.animateScrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
                .fillMaxSize()
                .padding(horizontal = 10.dp),
            autoCentering = AutoCenteringParams(itemOffset = scrollOffset),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            items(targets.size) { idx ->
                val value = targets[idx]

                BasicTextField(
                    value = value.name,
                    onValueChange = { newValue ->
                        val mutableTargets = targets.toMutableList()
                        mutableTargets[idx].name = newValue
                        targets = mutableTargets
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
                                settingsRepository.updateTargets(targets)
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
                            value.name,
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
                                val mutableTargets = targets.toMutableList()
                                mutableTargets.removeAt(idx)

                                targets = mutableTargets
                                scope.launch {
                                    settingsRepository.updateTargets(targets)
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
                        targets = targets + listOf(
                            Target(
                                name = "",
                                type = TargetType.SUBWAY,
                            )
                        )
                        scope.launch {
                            settingsRepository.updateTargets(targets)
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