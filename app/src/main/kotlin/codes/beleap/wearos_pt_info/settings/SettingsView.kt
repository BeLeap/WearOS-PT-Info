package codes.beleap.wearos_pt_info.settings

import android.widget.Toast
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import codes.beleap.wearos_pt_info.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SettingsView(navController: NavController, settingsRepository: SettingsRepository) {
    val listState = rememberScalingLazyListState()
    val vignettePosition by remember { mutableStateOf(VignettePosition.TopAndBottom) }

    var settings: Settings? by remember { mutableStateOf(null) }
    LaunchedEffect(key1 = Unit) {
        settings = settingsRepository.getSettings()
    }

    var versionTouchCount by remember { mutableStateOf(0) }
    val debugInfo = BuildConfig.SUBWAY_INFO_API_KEY

    Scaffold(
        vignette = { Vignette(vignettePosition = vignettePosition) },
        timeText = {
            TimeText()
        }
    ) {
        val itemSpacing = 6.dp
        val scope = rememberCoroutineScope()
        val focusRequester = remember { FocusRequester() }
        LaunchedEffect(key1 = Unit) {
            focusRequester.requestFocus()
        }

        ScalingLazyColumn(
            contentPadding = PaddingValues(top = 40.dp),
            state = listState,
            modifier = Modifier
                .onRotaryScrollEvent {
                    scope.launch {
                        listState.animateScrollBy(it.verticalScrollPixels)
                    }
                    true
                }
                .focusRequester(focusRequester)
                .focusable()
                .fillMaxSize(),
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
                    secondaryLabel = { Text("${settings?.count}") },
                    colors = ChipDefaults.secondaryChipColors(),
                )
            }

            item {
                Chip(
                    modifier = Modifier.fillMaxSize(),
                    onClick = { navController.navigate("settings/targets") },
                    label = { Text("대상 역") },
                    secondaryLabel = {
                        Text(
                            "${settings?.targets}",
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                    },
                    colors = ChipDefaults.secondaryChipColors(),
                )
            }

            item {
                val context = LocalContext.current

                Chip(
                    modifier = Modifier.fillMaxSize(),
                    onClick = {
                        versionTouchCount += 1
                        if (versionTouchCount >= 5) {
                            versionTouchCount = 0
                            if (settings?.isDebugMode == false) {
                                val toast = Toast.makeText(context, "Show Debug Info", Toast.LENGTH_SHORT)
                                toast.show()
                                settings = settings?.copy(
                                    isDebugMode = true,
                                )
                                scope.launch {
                                    settingsRepository.updateIsDebugMode(true)
                                }
                            } else {
                                val toast = Toast.makeText(context, "Remove Debug Info", Toast.LENGTH_SHORT)
                                toast.show()
                                settings = settings?.copy(
                                    isDebugMode = false
                                )
                                scope.launch {
                                    settingsRepository.updateIsDebugMode(false)
                                }
                            }
                        }
                    },
                    label = { Text("Version") },
                    secondaryLabel = { Text(BuildConfig.VERSION_NAME) },
                    colors = ChipDefaults.secondaryChipColors(),
                )
            }

            if (settings?.isDebugMode == true) {
                item {
                    Text(
                        debugInfo,
                        style = TextStyle(
                            fontSize = 9.sp,
                        ),
                    )
                }
            }
        }
    }
}