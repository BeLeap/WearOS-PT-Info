package codes.beleap.wearos_pt_info

import androidx.compose.runtime.Composable
import androidx.datastore.core.DataStore
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import codes.beleap.wearos_pt_info.settings.*

@Composable
fun MainView(settingStore: DataStore<Settings>) {
    val navController = rememberSwipeDismissableNavController()
    val settingsRepository = SettingsRepository(settingStore)

    SwipeDismissableNavHost(
        navController = navController,
        startDestination = "subway_info",
    ) {
        composable("subway_info") {
            SubwayArrivalInfoView(
                navController = navController,
                settingsRepository = settingsRepository,
            )
        }
        composable("settings") {
            SettingsView(
                navController = navController,
                settingsRepository = settingsRepository,
            )
        }
        composable("settings/count") {
            CountSettingView(
                settingsRepository = settingsRepository,
            )
        }
        composable("settings/target") {
            TargetSettingView(
                settingsRepository = settingsRepository,
            )
        }
    }
}