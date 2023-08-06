package codes.beleap.wearos_pt_info

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import codes.beleap.wearos_pt_info.components.CardInfo
import codes.beleap.wearos_pt_info.components.CardList
import codes.beleap.wearos_pt_info.dto.SubwayArrivalInfoResponse
import codes.beleap.wearos_pt_info.dto.mapSubwayIdToLineNumber
import codes.beleap.wearos_pt_info.repository.SubwayInfo
import kotlinx.coroutines.delay

@Composable
fun SubwayArrivalInfoView(
    listState: ScalingLazyListState,
    target: String,
    count: Int,
    isDebugMode: Boolean,
) {
    val apiKey = BuildConfig.SUBWAY_INFO_API_KEY
    val subwayInfoClient = SubwayInfo.buildClient(isDebugMode)

    var response: SubwayArrivalInfoResponse? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = Unit) {
        response = subwayInfoClient.getRealtimeArrivalInfo(apiKey, count, target)
    }

    val cards = response?.let { arrivalInfoResponse ->
        if (arrivalInfoResponse.status == 500 && arrivalInfoResponse.code == "INFO-200") {
            listOf(
                CardInfo(
                    title = arrivalInfoResponse.message,
                    content = {},
                )
            )
        } else {
            arrivalInfoResponse.realtimeArrivalList.map {
                CardInfo(
                    title = it.trainLineNm,
                    content = {
                        Column(
                            horizontalAlignment = Alignment.Start
                        ) {
                            var arriveIn by remember { mutableStateOf(it.barvlDt) }
                            LaunchedEffect(key1 = arriveIn) {
                                if (arriveIn > 0) {
                                    delay(1000L)
                                    arriveIn -= 1
                                }
                            }

                            Text(
                                mapSubwayIdToLineNumber(it.subwayId),
                                softWrap = true,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                style = TextStyle(
                                    fontSize = 15.sp,
                                    color = Color.Gray,
                                ),
                            )
                            Text(it.arvlMsg2)
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
                )
            }
        }
    }

    CardList(
        listState = listState,
        heading = "${target}역 지하철 도착 정보",
        cards = cards,
        isDebugMode = isDebugMode,
    )
}