package codes.beleap.wearos_pt_info

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
import androidx.compose.ui.input.rotary.onRotaryScrollEvent
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.ListHeader
import androidx.wear.compose.material.MaterialTheme
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.ScalingLazyListState
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TitleCard
import codes.beleap.wearos_pt_info.dto.BusArrivalInfoResponse
import codes.beleap.wearos_pt_info.repository.BusStationInfo
import kotlinx.coroutines.launch

@Composable
fun BusArrivalInfoView(
    listState: ScalingLazyListState,
    target: String,
    isDebugMode: Boolean,
) {
    val context = LocalContext.current
    val apiKey = BuildConfig.BUS_INFO_API_KEY
    val stationInfoClient = BusStationInfo.buildClient(isDebugMode)

    var response: BusArrivalInfoResponse? by remember {
        mutableStateOf(null)
    }

    val coroutineScope = rememberCoroutineScope()
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(key1 = Unit) {
        focusRequester.requestFocus()
        response = stationInfoClient.getStationByUid(apiKey, "24168")
    }

    val itemSpacing = 6.dp
    val scrollOffset = with(LocalDensity.current) {
        -(itemSpacing / 2).roundToPx()
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
        verticalArrangement = Arrangement.spacedBy(itemSpacing)
    ) {
        item {
            ListHeader {
                Text(
                    text = "${target}역 버스 도착 정보",
                    textAlign = TextAlign.Center,
                )
            }
        }

        response?.body?.let {
            items(it.size) { idx ->
                val info = it[idx]

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
                            text = info.routeName,
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
                        Text(text = info.arrivalMessage)
                    }
                }
            }
        }
    }
}