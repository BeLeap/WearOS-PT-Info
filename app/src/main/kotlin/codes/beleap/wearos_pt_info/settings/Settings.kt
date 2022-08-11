package codes.beleap.wearos_pt_info.settings

import androidx.datastore.core.CorruptionException
import androidx.datastore.core.Serializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.io.InputStream
import java.io.OutputStream

@Serializable
data class Settings(
    val count: Int = 5,
    @Deprecated(message = "Use targets")
    val target: String? = null,
    val targets: List<String> = listOf("서울"),
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
        output.write(Json.encodeToString(Settings.serializer(), t).encodeToByteArray())
    }
}
