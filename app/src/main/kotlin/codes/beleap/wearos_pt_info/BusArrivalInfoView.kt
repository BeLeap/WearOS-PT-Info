package codes.beleap.wearos_pt_info

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import codes.beleap.wearos_pt_info.components.CardInfo
import codes.beleap.wearos_pt_info.components.CardList
import codes.beleap.wearos_pt_info.dto.BusArrivalInfoResponse
import codes.beleap.wearos_pt_info.repository.BusStationInfo

@Composable
fun BusArrivalInfoView(
    listState: ScalingLazyListState,
    target: String,
    isDebugMode: Boolean,
) {
    val apiKey = BuildConfig.BUS_INFO_API_KEY
    val stationInfoClient = BusStationInfo.buildClient(isDebugMode)

    var response: BusArrivalInfoResponse? by remember {
        mutableStateOf(null)
    }

    LaunchedEffect(key1 = Unit) {
        response = stationInfoClient.getStationByUid(apiKey, target)
    }


    CardList(
        listState = listState,
        heading = "${target}역 버스 도착 정보",
        cards = response?.body?.map {
            CardInfo(
                title = it.routeName,
                content = {
                    Column(
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(text = it.arrivalMessage)
                    }
                }
            )
        },
        isDebugMode = isDebugMode,
    )
}