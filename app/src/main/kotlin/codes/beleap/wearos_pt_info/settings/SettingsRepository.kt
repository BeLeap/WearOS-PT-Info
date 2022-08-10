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
    suspend fun updateTarget(target: String) = settingsStore.updateData {
        it.copy(target = target)
    }
    suspend fun updateIsDebugMode(target: Boolean) = settingsStore.updateData {
        it.copy(isDebugMode = target)
    }
}