package codes.beleap.wearos_pt_info.settings

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import codes.beleap.wearos_pt_info.BuildConfig

@Composable
fun SettingsView(navController: NavController, settingsRepository: SettingsRepository) {
    val listState = rememberScalingLazyListState()
    val vignettePosition = remember { mutableStateOf(VignettePosition.TopAndBottom) }

    val settings: MutableState<Settings?> = remember { mutableStateOf(null) }
    LaunchedEffect(key1 = null) {
        settings.value = settingsRepository.getSettings()
    }

    val versionTouchCount = remember { mutableStateOf(0) }
    val debugInfo: MutableState<String?> = remember { mutableStateOf(null) }

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

            item {
                val context = LocalContext.current

                Chip(
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        versionTouchCount.value += 1
                        if (versionTouchCount.value >= 5) {
                            versionTouchCount.value = 0
                            if (debugInfo.value == null) {
                                val toast = Toast.makeText(context, "Show Debug Info", Toast.LENGTH_SHORT)
                                toast.show()
                                debugInfo.value = BuildConfig.SUBWAY_INFO_API_KEY
                            } else {
                                val toast = Toast.makeText(context, "Remove Debug Info", Toast.LENGTH_SHORT)
                                toast.show()
                                debugInfo.value = null
                            }
                        }
                    },
                    label = {
                        Text(
                            "Version: ${BuildConfig.VERSION_NAME}",
                            style = TextStyle(
                                fontWeight = FontWeight.Normal,
                            ),
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                )
            }

            debugInfo.value?.let {
                item {
                    Text(
                        it,
                        style = TextStyle(
                            fontSize = 9.sp,
                        ),
                    )
                }
            }
        }
    }
}