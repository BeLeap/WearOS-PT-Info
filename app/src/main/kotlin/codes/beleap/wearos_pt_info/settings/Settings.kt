package codes.beleap.wearos_pt_info.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
enum class TargetType {
    SUBWAY,
    BUS,
}
fun TargetType.toLabel() = when (this) {
    TargetType.SUBWAY -> "지하철"
    TargetType.BUS -> "버스"
}

@Serializable
data class Target(
    var name: String,
    val type: TargetType,
)

@Serializable
data class Settings(
    val count: Int = 5,
    val targets: List<Target> = listOf(
        Target(
            name = "서울",
            type = TargetType.SUBWAY
        )
    ),
    val isDebugMode: Boolean? = false,
)

object SettingsSerializer: Serializer<Settings> {
    override val defaultValue: Settings = Settings()

    override suspend fun readFrom(input: InputStream): Settings {
        try {
            return Json.decodeFromString(
                Settings.serializer(),
                input.readBytes().decodeToString(),
            )
        } catch (e: SerializationException) {
            throw CorruptionException("Unable to read settings", e)
        }
    }

    override suspend fun writeTo(t: Settings, output: OutputStream) {
        withContext(Dispatchers.IO) {
            output.write(Json.encodeToString(Settings.serializer(), t).encodeToByteArray())
        }
    }
}
