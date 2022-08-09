package codes.beleap.wearos_pt_info

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.*
import androidx.compose.ui.tooling.preview.Preview
import androidx.wear.compose.material.Text
import codes.beleap.wearos_pt_info.network.SubwayArrivalInfoApi
import codes.beleap.wearos_pt_info.network.SubwayArrivalInfoResponse

@Preview
@Composable
fun MainView() {
    val response: MutableState<SubwayArrivalInfoResponse?> = remember { mutableStateOf(null) }

    LaunchedEffect(key1 = "") {
        val apiService = SubwayArrivalInfoApi.retrofitService
        response.value = apiService.getSubwayArrivalInfo()
    }

    Column {
        Text("Lorem Ipsum")
        Text("Dolor Sit")
        Text(response.value?.errorMessage?.message ?: "Loading")
    }
}