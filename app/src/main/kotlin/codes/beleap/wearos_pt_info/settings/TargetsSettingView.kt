package codes.beleap.wearos_pt_info.settings

import androidx.compose.ui.focus.FocusRequester
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.navigation.NavController
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ButtonDefaults
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Picker
import androidx.wear.compose.material.PositionIndicator
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition
import androidx.wear.compose.material.rememberPickerState
import androidx.wear.compose.material.rememberScalingLazyListState
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import kotlinx.coroutines.launch

@Composable
fun TargetsSettingView(
    settingsRepository: SettingsRepository,
    mainNavController: NavController,
) {
    val listState = rememberScalingLazyListState()
    val vignettePosition by remember { mutableStateOf(VignettePosition.TopAndBottom) }

    val navController = rememberSwipeDismissableNavController()

    var targets: List<Target> by remember { mutableStateOf(listOf()) }
    LaunchedEffect(key1 = Unit) {
        targets = settingsRepository.getSettings().targets.toMutableList()
    }


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
        SwipeDismissableNavHost(navController = navController, startDestination = "view") {
            composable("edit/type/{index}") {
                val idx = it.arguments?.getString("index")!!.toInt()
                val target = targets[idx]

                val pickerState = rememberPickerState(
                    initialNumberOfOptions = TargetType.values().size,
                    initiallySelectedOption = target.type.ordinal,
                    repeatItems = false,
                )
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Text(target.name)
                    Picker(
                        state = pickerState,
                        contentDescription = "Select target type",
                        modifier = Modifier.size(100.dp),
                    ) {optionIdx ->
                        Text(
                            text = TargetType.values()[optionIdx].toLabel(),
                            fontSize = 5.em,
                        )
                    }
                    CompactButton(onClick = {
                        val mutableTargets = targets.toMutableList()
                        mutableTargets[idx] = target.copy(type = TargetType.values()[pickerState.selectedOption])
                        targets = mutableTargets
                        navController.popBackStack()
                    }) {
                        Icon(Icons.Rounded.Save, "Save")
                    }
                }
            }

            composable("view"){
                val scope = rememberCoroutineScope()

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
                        val target = targets[idx]

                        Row(
                            modifier = Modifier.fillMaxSize(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Chip(
                                onClick = {
                                  navController.navigate("edit/type/$idx")
                                },
                                label = {
                                    Text(text = target.type.toLabel())
                                }
                            )

                            var nameValue by rememberSaveable(stateSaver = TextFieldValue.Saver) {
                                mutableStateOf(TextFieldValue(
                                    text = target.name,
                                ))
                            }
                            BasicTextField(
                                value = nameValue,
                                onValueChange = { newValue ->
                                    nameValue = newValue
                                },
                                keyboardOptions = KeyboardOptions(
                                    imeAction = ImeAction.Done,
                                ),
                                keyboardActions = KeyboardActions(
                                    onDone = {
                                        val mutableTargets = targets.toMutableList()
                                        mutableTargets[idx] = target.copy(name = nameValue.text)
                                        targets = mutableTargets
                                        focusManager.clearFocus()
                                    }
                                ),
                                singleLine = true,
                            ) {
                                Text(
                                    nameValue.text,
                                    modifier = Modifier
                                        .padding(
                                            horizontal = 25.dp,
                                        ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }

                            CompactButton(
                                onClick = {
                                    val mutableTargets = targets.toMutableList()
                                    mutableTargets.removeAt(idx)
                                    targets = mutableTargets
                                },
                                colors = ButtonDefaults.secondaryButtonColors()
                            ) {
                                Icon(Icons.Rounded.Remove, "Remove $idx")
                            }
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.SpaceAround,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            CompactButton(
                                onClick = {
                                    targets = targets + listOf(
                                        Target(
                                            name = "",
                                            type = TargetType.SUBWAY,
                                        )
                                    )
                                },
                                colors = ButtonDefaults.secondaryButtonColors()
                            ) {
                                Icon(Icons.Rounded.Add, "Add")
                            }

                            CompactButton(
                                onClick = {
                                    scope.launch {
                                        settingsRepository.updateTargets(targets.toList())
                                    }
                                    mainNavController.popBackStack()
                                },
                            ) {
                                Icon(Icons.Rounded.Save, "Save")
                            }
                        }
                    }
                }
            }
        }
    }
}