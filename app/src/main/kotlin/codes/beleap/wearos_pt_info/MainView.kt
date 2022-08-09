package codes.beleap.wearos_pt_info

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.*
import codes.beleap.wearos_pt_info.network.SubwayArrivalInfoApi
import codes.beleap.wearos_pt_info.network.SubwayArrivalInfoResponse
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

@Preview
@Composable
fun MainView() {
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

        LaunchedEffect(key1 = null) {
            val apiService = SubwayArrivalInfoApi.retrofitService
            response.value = apiService.getSubwayArrivalInfo()
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
                    Text("서울역 지하철 도착 정보")
                }
            }
            items(20) {
                Chip(
                    onClick = {
                       coroutineScope.launch {
                           listState.animateScrollToItem(it + 1, scrollOffset)
                       }
                    },
                    label = { Text("Item No.$it") },
                    colors = ChipDefaults.secondaryChipColors(),
                )
            }
        }
    }
}