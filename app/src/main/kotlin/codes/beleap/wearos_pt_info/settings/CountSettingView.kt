package codes.beleap.wearos_pt_info.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.Icon
import androidx.wear.compose.material.Stepper
import androidx.wear.compose.material.StepperDefaults
import androidx.wear.compose.material.Text
import kotlinx.coroutines.launch

@Composable
fun CountSettingView(
    settingsRepository: SettingsRepository,
) {
    val scope = rememberCoroutineScope()

    val count: MutableState<Int?> = remember { mutableStateOf(null) }
    LaunchedEffect(key1 = null) {
        count.value = settingsRepository.getSettings().count
    }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        count.value?.let {
            Stepper(
                value = it,
                onValueChange = { newValue ->
                    count.value = newValue
                    scope.launch {
                        settingsRepository.updateCount(newValue)
                    }
                },
                valueProgression = 1..5,
                increaseIcon = { Icon(StepperDefaults.Increase, "Increase") },
                decreaseIcon = { Icon(StepperDefaults.Decrease, "Decrease") },
            ) {
                Text(
                    "${it}개",
                    style = TextStyle(
                        fontSize = 20.sp,
                    ),
                )
            }
        }
    }
}