package codes.beleap.wearos_pt_info

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.core.DataStore
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import codes.beleap.wearos_pt_info.network.SubwayArrivalInfoApi
import codes.beleap.wearos_pt_info.network.SubwayArrivalInfoResponse
import codes.beleap.wearos_pt_info.network.mapSubwayIdToLineNumber
import codes.beleap.wearos_pt_info.settings.Settings
import codes.beleap.wearos_pt_info.settings.SettingsRepository
import com.squareup.moshi.JsonDataException
import kotlinx.coroutines.launch

@Composable
fun SubwayArrivalInfoView(
    navController: NavController,
    settingsRepository: SettingsRepository,
) {
    val listState = rememberScalingLazyListState()
    val vignettePosition = remember { mutableStateOf(VignettePosition.TopAndBottom) }

    Scaffold(
        positionIndicator = {
            PositionIndicator(
                scalingLazyListState = listState,
                modifier = Modifier,
            )
        },
        vignette = { Vignette(vignettePosition = vignettePosition.value) },
        timeText = {
            TimeText()
        }
    ) {
        val response: MutableState<SubwayArrivalInfoResponse?> = remember { mutableStateOf(null) }
        val settings: MutableState<Settings> = remember { mutableStateOf(Settings.default()) }

        LaunchedEffect(key1 = null) {
            settings.value = settingsRepository.getSettings()

            val apiService = SubwayArrivalInfoApi.retrofitService
            try {
                response.value = apiService.getSubwayArrivalInfo(
                    count = settings.value.count,
                    target = settings.value.target,
                )
                Log.d("DataFetcher", response.value.toString())
            } catch (e: JsonDataException) {
                Log.w("DataFetcher", "Malformed response", e)
            }
        }

        val coroutineScope = rememberCoroutineScope()
        val itemSpacing = 6.dp
        val scrollOffset = with(LocalDensity.current) {
            -(itemSpacing / 2).roundToPx()
        }

        ScalingLazyColumn(
            contentPadding = PaddingValues(top = 40.dp),
            state = listState,
            modifier = Modifier.fillMaxSize(),
            autoCentering = AutoCenteringParams(itemOffset = scrollOffset),
            verticalArrangement = Arrangement.spacedBy(itemSpacing),
        ) {
            item {
                ListHeader {
                    Text("${settings.value.target}역 지하철 도착 정보")
                }
            }

            response.value?.realtimeArrivalList?.let {
                items(it.size) { idx ->
                    val info = it[idx]

                    TitleCard(
                        onClick = {
                            coroutineScope.launch {
                                listState.animateScrollToItem(idx + 1, scrollOffset)
                            }
                        },
                        title = {
                            Text(
                                info.trainLineNm,
                                softWrap = true,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        },
                        contentColor = MaterialTheme.colors.onSurface,
                        titleColor = MaterialTheme.colors.onSurface,
                    ) {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            Text(
                                mapSubwayIdToLineNumber(info.subwayId),
                                softWrap = true,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = 10.sp,
                                    color = Color.Gray,
                                ),
                            )
                            Text(
                                info.arvlMsg2,
                                softWrap = true,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                            )
                        }
                    }
                }
            }

            item {
                CompactButton(onClick = {
                    navController.navigate("settings")
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