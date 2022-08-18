package codes.beleap.wearos_pt_info

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import codes.beleap.wearos_pt_info.settings.Settings
import codes.beleap.wearos_pt_info.settings.SettingsRepository
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SubwayArrivalInfoNavView(
    listState: ScalingLazyListState,
    mainNavController: NavController,
    settingsRepository: SettingsRepository,
) {
    val infoNavController = rememberSwipeDismissableNavController()
    var settings by remember { mutableStateOf(Settings()) }

    LaunchedEffect(key1 = settingsRepository) {
        settings = settingsRepository.getSettings()
    }

    SwipeDismissableNavHost(navController = infoNavController, startDestination = "info_list") {
        composable("info_list") {
            val itemSpacing = 1.dp
            val scrollOffset = with(LocalDensity.current) {
                -(itemSpacing / 2).roundToPx()
            }

            val focusRequester = remember { FocusRequester() }
            val scope = rememberCoroutineScope()
            LaunchedEffect(key1 = Unit) {
                focusRequester.requestFocus()
            }

            ScalingLazyColumn(
                contentPadding = PaddingValues(top = 25.dp),
                state = listState,
                modifier = Modifier
                    .onRotaryScrollEvent {
                        scope.launch {
                            listState.scrollBy(it.verticalScrollPixels)
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
                items(settings.targets.size) { idx ->
                    CompactChip(
                        onClick = {
                            infoNavController.navigate("info/$idx")
                        },
                        colors = ChipDefaults.secondaryChipColors(),
                        modifier = Modifier
                            .fillMaxSize(),
                        label = {
                            Text(
                                settings.targets[idx],
                                textAlign = TextAlign.Center,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    )
                }

                item {
                    CompactButton(onClick = {
                        mainNavController.navigate("settings")
                    }) {
                        Icon(
                            Icons.Rounded.Settings,
                            contentDescription = "Settings",
                        )
                    }
                }
            }
        }
        composable("info/{index}") {
            SubwayArrivalInfoView(
                listState = listState,
                target = settings.targets[it.arguments?.getString("index")!!.toInt()],
                count = settings.count,
                isDebugMode = settings.isDebugMode ?: false,
            )
        }
    }
}