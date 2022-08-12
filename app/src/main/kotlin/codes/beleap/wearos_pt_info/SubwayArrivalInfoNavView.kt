package codes.beleap.wearos_pt_info

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
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

@Composable
fun SubwayArrivalInfoNavView(
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

                val itemSpacing = 1.dp
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
        }
        composable("info/{index}") {
            SubwayArrivalInfoView(
                target = settings.targets[it.arguments?.getString("index")!!.toInt()],
                count = settings.count,
                isDebugMode = settings.isDebugMode ?: false,
            )
        }
    }
}