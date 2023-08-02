package codes.beleap.wearos_pt_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.datastore.core.DataStore
import androidx.wear.compose.material.*
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import codes.beleap.wearos_pt_info.settings.*

@Composable
fun MainView(settingStore: DataStore<Settings>) {
    val navController = rememberSwipeDismissableNavController()
    val settingsRepository = SettingsRepository(settingStore)

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
        timeText = { TimeText() },
    ) {
        SwipeDismissableNavHost(
            navController = navController,
            startDestination = "arrival_info",
        ) {
            composable("arrival_info") {
                ArrivalInfoNavView(
                    listState = listState,
                    mainNavController = navController,
                    settingsRepository = settingsRepository,
                )
            }
            composable("settings") {
                SettingsView(
                    listState = listState,
                    navController = navController,
                    settingsRepository = settingsRepository,
                )
            }
            composable("settings/count") {
                CountSettingView(
                    settingsRepository = settingsRepository,
                )
            }
            composable("settings/targets") {
                TargetsSettingView(
                    settingsRepository = settingsRepository,
                    mainNavController = navController,
                )
            }
            composable("privacy_policy") {
                PrivacyPolicy()
            }
        }
    }
}