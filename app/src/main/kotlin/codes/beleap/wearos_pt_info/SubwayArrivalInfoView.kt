package codes.beleap.wearos_pt_info

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.wear.compose.material.*
import codes.beleap.wearos_pt_info.network.SubwayArrivalInfoResponse
import codes.beleap.wearos_pt_info.network.mapSubwayIdToLineNumber
import codes.beleap.wearos_pt_info.settings.Settings
import codes.beleap.wearos_pt_info.settings.SettingsRepository
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.JsonRequest
import com.android.volley.toolbox.StringRequest
import com.android.volley.toolbox.Volley
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
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
        val apiKey = BuildConfig.SUBWAY_INFO_API_KEY
        val context = LocalContext.current
        val requestQueue = Volley.newRequestQueue(context)


        LaunchedEffect(key1 = Unit) {
            try {
                val settingsValue = settingsRepository.getSettings()

                val showDebugToast = { message: String ->
                    if (settingsValue.isDebugMode == true) {
                        val toast =
                            Toast.makeText(context, message, Toast.LENGTH_SHORT)
                        toast.show()
                    }
                }
                showDebugToast(settingsValue.toString())

                settings.value = settingsValue

                val url = "http://swopenapi.seoul.go.kr/api/subway/${apiKey}/json/realtimeStationArrival/0/${settingsValue.count}/${settingsValue.target}"

                val request = JsonObjectRequest(url,
                    { resp ->
                        try {
                            val info = SubwayArrivalInfoResponse.fromJsonObject(resp)
                            showDebugToast(info.errorMessage.message)
                            Log.d("DataFetcher", info.toString())

                            response.value = info
                        } catch (error: JsonSyntaxException) {
                            showDebugToast("Malformed Response: ${error.cause}: ${error.message}")
                            showDebugToast(resp.toString())
                        }
                    },
                    { error ->
                        showDebugToast("${error.cause}: ${error.message}")
                    }
                )
                requestQueue.add(request)
            } catch (e: Exception) {
                val toast = Toast.makeText(context, "Failed to read settings: ${e.cause}: ${e.message}", Toast.LENGTH_SHORT)
                toast.show()
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
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 10.dp),
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
            } ?: item {
                CircularProgressIndicator()
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