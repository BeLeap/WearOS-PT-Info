package codes.beleap.wearos_pt_info

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.datastore.dataStore
import codes.beleap.wearos_pt_info.settings.SettingsSerializer

val Context.settingsStore by dataStore("settings.json", serializer = SettingsSerializer)

class MainActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainView(settingsStore)
        }
    }
}