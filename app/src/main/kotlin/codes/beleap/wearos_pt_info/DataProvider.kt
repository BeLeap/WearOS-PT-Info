package codes.beleap.wearos_pt_info

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Scaffold
import androidx.wear.compose.material.ScalingLazyColumn
import androidx.wear.compose.material.Text
import androidx.wear.compose.material.TimeText
import androidx.wear.compose.material.Vignette
import androidx.wear.compose.material.VignettePosition

@Composable
fun DataProvider() {
    val vignettePosition by remember { mutableStateOf(VignettePosition.TopAndBottom) }

    Scaffold(
        vignette = { Vignette(vignettePosition = vignettePosition) },
        timeText = {
            TimeText()
        }
    ) {
        ScalingLazyColumn(
            contentPadding = PaddingValues(vertical = 40.dp),
            modifier = Modifier
                .padding(5.dp),
        ) {
            item {
                Text(
                    text = "정보 제공처",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    ),
                )
            }

            item {
                Text(
                    text = "지하철 도착 정보 / 버스 도착 정보\n" +
                            "\n" +
                            "서울특별시 도시교통실 교통기획관 미래첨단교통과, https://data.seoul.go.kr / https://data.go.kr\n"
                )
            }
        }
    }
}