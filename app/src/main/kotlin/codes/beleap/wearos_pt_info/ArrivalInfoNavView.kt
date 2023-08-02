package codes.beleap.wearos_pt_info

import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.toLowerCase
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import codes.beleap.wearos_pt_info.settings.Settings
import codes.beleap.wearos_pt_info.settings.SettingsRepository
import codes.beleap.wearos_pt_info.settings.TargetType
import codes.beleap.wearos_pt_info.settings.toLabel
import kotlinx.coroutines.launch

@Composable
fun ArrivalInfoNavView(
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
                    val target = settings.targets[idx]

                    CompactChip(
                        onClick = {
                            infoNavController.navigate("info/${target.type.name.lowercase()}/$idx")
                        },
                        colors = ChipDefaults.secondaryChipColors(),
                        modifier = Modifier
                            .fillMaxSize(),
                        label = {
                            Text(
                                "${target.type.toLabel()} - ${target.name}",
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
        composable("info/subway/{index}") {
            SubwayArrivalInfoView(
                listState = listState,
                target = settings.targets[it.arguments?.getString("index")!!.toInt()].name,
                count = settings.count,
                isDebugMode = settings.isDebugMode ?: false,
            )
        }
    }
}