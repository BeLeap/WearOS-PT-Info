package codes.beleap.wearos_pt_info

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.AutoCenteringParams
import androidx.wear.compose.material.CircularProgressIndicator
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import codes.beleap.wearos_pt_info.dto.SubwayArrivalInfoResponse
import codes.beleap.wearos_pt_info.dto.mapSubwayIdToLineNumber
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONException
import java.time.Duration
import java.time.LocalDateTime

@Composable
fun SubwayArrivalInfoView(
    listState: ScalingLazyListState,
    target: String,
    count: Int,
    isDebugMode: Boolean,
) {
    var response: SubwayArrivalInfoResponse? by remember { mutableStateOf(null) }
    val apiKey = BuildConfig.SUBWAY_INFO_API_KEY
    val context = LocalContext.current
    val showDebugToast = { message: String ->
        if (isDebugMode) {
            val toast =
                Toast.makeText(context, message, Toast.LENGTH_SHORT)
            toast.show()
        }
    }

    val requestQueue = Volley.newRequestQueue(LocalContext.current)

    val url = "http://swopenapi.seoul.go.kr/api/subway/${apiKey}/json/realtimeStationArrival/0/${count}/${target}"

    val request = JsonObjectRequest(
        url,
        { resp ->
            try {
                val info = SubwayArrivalInfoResponse.fromJsonObject(resp)
                showDebugToast(info.errorMessage.message)
                Log.d("DataFetcher", info.toString())

                response = info.copy(
                    realtimeArrivalList = info.realtimeArrivalList
                        .map {
                            if (it.barvlDt > 0) {
                                it.copy(
                                    barvlDt = it.barvlDt - (Duration.between(
                                        LocalDateTime.now(),
                                        it.recptnDt
                                    )).seconds
                                )
                            } else {
                                it
                            }
                        }
                )
            } catch (error: JSONException) {
                showDebugToast("Malformed Response: ${error.cause}: ${error.message}")
                showDebugToast(resp.toString())
            }
        },
        { error ->
            showDebugToast("${error.cause}: ${error.message}")
        }
    )
    Log.d("DataFetcher", "RequestURL: $url")
    LaunchedEffect(key1 = response) {
        delay(10000L)
        requestQueue.add(request)
    }

    val coroutineScope = rememberCoroutineScope()
    val itemSpacing = 6.dp
    val scrollOffset = with(LocalDensity.current) {
        -(itemSpacing / 2).roundToPx()
    }

    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
    }

    ScalingLazyColumn(
        contentPadding = PaddingValues(top = 40.dp),
        state = listState,
        modifier = Modifier
            .onRotaryScrollEvent {
                coroutineScope.launch {
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
        item {
            ListHeader {
                Text(
                    "${target}역 지하철 도착 정보",
                    textAlign = TextAlign.Center,
                )
            }
        }

        response
            ?.realtimeArrivalList
            ?.let {
                items(it.size) { idx ->
                    val info = it[idx]

                    var arriveIn by remember { mutableStateOf(info.barvlDt) }
                    LaunchedEffect(key1 = arriveIn) {
                        if (arriveIn > 0) {
                            delay(1000L)
                            arriveIn -= 1
                        }
                    }

                    TitleCard(
                        onClick = {
                            if (isDebugMode) {
                                val toast = Toast.makeText(context, info.toString(), Toast.LENGTH_SHORT)
                                toast.show()
                            }
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
                                    fontSize = 15.sp,
                                    color = Color.Gray,
                                ),
                            )
                            Text(info.arvlMsg2)
                            if (arriveIn > 0) {
                                Text(
                                    "${arriveIn}초 후 도착",
                                    style = TextStyle(
                                        fontSize = 13.sp,
                                        color = Color.LightGray,
                                    ),
                                )
                            }
                        }
                    }
                }
            } ?: item {
            CircularProgressIndicator()
        }
    }
}