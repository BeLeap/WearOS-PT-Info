package codes.beleap.wearos_pt_info

import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.*

@Composable
fun PrivacyPolicy() {
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
                    text = "개인정보처리방침",
                    style = TextStyle(
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                    ),
                )
            }

            item {
                Text(
                    text = "1. 개인정보의 처리 목적 (‘WearOS-PT-Info’’이하 ‘App’) 은(는) 다음의 목적을 위하여 개인정보를 처리하고 있지 않습니다.\n" +
                            "\n" +
                            "2. 개인정보 보호책임자 작성\n" +
                            "① App은(는) 개인정보 처리에 관한 업무를 총괄해서 책임지고, 개인정보 처리와 관련한 정보주체의 불만처리 및 피해구제 등을 위하여 아래와 같이 개인정보 보호책임자를 지정하고 있습니다.\n" +
                            "\n" +
                            "▶ 개인정보 보호책임자\n" +
                            "성명 : 장창서\n" +
                            "연락처 : beleap@beleap.codes\n" +
                            "※ 개인정보 보호 담당자로 연결됩니다.\n" +
                            "\n" +
                            "3. 개인정보 처리방침 변경\n" +
                            "①이 개인정보처리방침은 시행일로부터 적용되며, 법령 및 방침에 따른 변경내용의 추가, 삭제 및 정정이 있는 경우에는 변경사항의 시행 7일 전부터 앱 내 채널을 통하여 고지할 것입니다.\n"
                )
            }
        }
    }
}