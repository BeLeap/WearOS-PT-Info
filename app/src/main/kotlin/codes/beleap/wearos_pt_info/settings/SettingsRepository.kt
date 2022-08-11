package codes.beleap.wearos_pt_info.settings

import androidx.datastore.core.DataStore
import kotlinx.coroutines.flow.first

class SettingsRepository(
    private val settingsStore: DataStore<Settings>
) {
    suspend fun getSettings() = settingsStore.data.first()

    suspend fun updateCount(count: Int) = settingsStore.updateData {
        it.copy(count = count)
    }
    suspend fun updateTargets(targets: List<String>) = settingsStore.updateData { data ->
        data.copy(
            target = null,
            targets = targets.filter { it.isNotBlank() },
        )
    }
    suspend fun updateIsDebugMode(isDebugMode: Boolean) = settingsStore.updateData {
        it.copy(isDebugMode = isDebugMode)
    }
}