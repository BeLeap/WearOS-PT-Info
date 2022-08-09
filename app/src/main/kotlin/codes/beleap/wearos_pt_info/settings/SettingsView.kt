package codes.beleap.wearos_pt_info.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.wear.compose.material.*

@Composable
fun SettingsView(navController: NavController, settingsRepository: SettingsRepository) {
    val listState = rememberScalingLazyListState()
    val vignettePosition = remember { mutableStateOf(VignettePosition.TopAndBottom) }

    val settings: MutableState<Settings?> = remember { mutableStateOf(null) }
    LaunchedEffect(key1 = null) {
        settings.value = settingsRepository.getSettings()
    }

    Scaffold(
        vignette = { Vignette(vignettePosition = vignettePosition.value) },
        timeText = {
            TimeText()
        }
    ) {
        val itemSpacing = 6.dp

        ScalingLazyColumn(
            contentPadding = PaddingValues(top = 40.dp),
            state = listState,
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            item {
                ListHeader {
                    Text("설정")
                }
            }
            
            item {
                Chip(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { navController.navigate("settings/count") },
                    label = { Text("개수 설정") },
                    secondaryLabel = { Text("${settings.value?.count}") },
                    colors = ChipDefaults.secondaryChipColors(),
                )
            }

            item {
                Chip(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { navController.navigate("settings/target") },
                    label = { Text("대상 역") },
                    secondaryLabel = { Text("${settings.value?.target}") },
                    colors = ChipDefaults.secondaryChipColors(),
                )
            }
        }
    }
}