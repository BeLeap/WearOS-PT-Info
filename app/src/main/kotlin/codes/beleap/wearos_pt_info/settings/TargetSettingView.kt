package codes.beleap.wearos_pt_info.settings

import android.util.Log
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Text
import kotlinx.coroutines.launch

@Composable
fun TargetSettingView(
    settingsRepository: SettingsRepository,
) {
    val scope = rememberCoroutineScope()

    val target: MutableState<String?> = remember { mutableStateOf(null) }
    LaunchedEffect(key1 = null) {
        target.value = settingsRepository.getSettings().target
        Log.d("TEST", target.value ?: "")
        Log.d("TEST", target.value?.length.toString())
    }

    val focusManager = LocalFocusManager.current

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        target.value?.let {
            val targetValue = it

            BasicTextField(
                value = it,
                onValueChange = { newValue ->
                    target.value = newValue
                },
                modifier = Modifier
                    .border(BorderStroke(2.dp, Color.Gray), RoundedCornerShape(16.dp)),
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Done
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        scope.launch {
                            settingsRepository.updateTarget(targetValue)
                        }
                        focusManager.clearFocus()
                    }
                ),
                singleLine = true,
            ) {
                Text(
                    targetValue,
                    modifier = Modifier
                        .padding(
                            horizontal = 25.dp,
                            vertical = 5.dp,
                        ),
                )
            }
        }
    }
}