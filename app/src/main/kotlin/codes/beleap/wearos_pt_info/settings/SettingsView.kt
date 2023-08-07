package codes.beleap.wearos_pt_info.settings

import android.widget.Toast
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.animateScrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.ClickableText
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import codes.beleap.wearos_pt_info.BuildConfig
import kotlinx.coroutines.launch

@Composable
fun SettingsView(
    listState: ScalingLazyListState,
    navController: NavController,
    settingsRepository: SettingsRepository,
) {
    var settings: Settings? by remember { mutableStateOf(null) }
    LaunchedEffect(key1 = Unit) {
        settings = settingsRepository.getSettings()
    }

    var versionTouchCount by remember { mutableStateOf(0) }
    val subwayApiKey = BuildConfig.SUBWAY_INFO_API_KEY
    val busApiKey = BuildConfig.BUS_INFO_API_KEY

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
                label = { Text("대상") },
                secondaryLabel = {
                    Text(
                        settings?.targets?.joinToString(" / ") { "${it.type.toLabel()} - ${it.name}" }
                            ?: "",
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

        item {
            ClickableText(
                text = AnnotatedString(
                    "개인정보처리방침",
                    spanStyle = SpanStyle(
                        color = Color.White,
                        textDecoration = TextDecoration.Underline,
                    ),
                ),
                onClick = {
                    navController.navigate("privacy_policy")
                },
            )
        }


        if (settings?.isDebugMode == true) {
            item {
                Text(
                    subwayApiKey,
                    style = TextStyle(
                        fontSize = 9.sp,
                    ),
                )
            }
            item {
                Text(
                    busApiKey,
                    style = TextStyle(
                        fontSize = 9.sp,
                    ),
                )
            }
        }
    }
}