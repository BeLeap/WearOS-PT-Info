package codes.beleap.wearos_pt_info.dto

import org.json.JSONObject
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

data class SubwayArrivalInfoResponse(
    val errorMessage: ErrorMessage,
    val realtimeArrivalList: List<SubwayArrivalInfo>,
) {
    companion object {
        fun fromJsonObject(jsonObject: JSONObject): SubwayArrivalInfoResponse {
            val rawList = jsonObject.optJSONArray("realtimeArrivalList")
            var list: List<SubwayArrivalInfo> = listOf()
            if (rawList != null) {
                for (idx in (0 until rawList.length())) {
                    list = list + SubwayArrivalInfo.fromJsonObject(rawList.getJSONObject(idx))
                }
            }

            return SubwayArrivalInfoResponse(
                errorMessage = ErrorMessage.fromJsonObject(jsonObject.getJSONObject("errorMessage")),
                realtimeArrivalList = list
            )
        }
    }
}

data class ErrorMessage(
    val status: Int,
    val message: String,
) {
    companion object {
        fun fromJsonObject(jsonObject: JSONObject): ErrorMessage = ErrorMessage(
            status = jsonObject.getInt("status"),
            message = jsonObject.getString("message"),
        )
    }
}

data class SubwayArrivalInfo(
    val subwayId: String,
    val trainLineNm: String,
    var barvlDt: Long,
    val recptnDt: LocalDateTime,
    val arvlMsg2: String,
) {
    companion object {
        private val localDatePattern = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        fun fromJsonObject(jsonObject: JSONObject): SubwayArrivalInfo = SubwayArrivalInfo(
            subwayId = jsonObject.getString("subwayId"),
            trainLineNm = jsonObject.getString("trainLineNm"),
            barvlDt = jsonObject.getLong("barvlDt"),
            recptnDt = LocalDateTime.parse(jsonObject.getString("recptnDt").split('.').first(), localDatePattern),
            arvlMsg2 = jsonObject.getString("arvlMsg2"),
        )
    }
}

fun mapSubwayIdToLineNumber(subwayId: String): String = when (subwayId) {
    "1001" -> "1호선"
    "1002" -> "2호선"
    "1003" -> "3호선"
    "1004" -> "4호선"
    "1005" -> "5호선"
    "1006" -> "6호선"
    "1007" -> "7호선"
    "1008" -> "8호선"
    "1009" -> "9호선"
    "1063" -> "경의중앙선"
    "1065" -> "공항철도"
    "1075" -> "분당선"
    "1077" -> "신분당선"
    "1091" -> "자기부상선"
    "1092" -> "우이신설선"
    "1167" -> "경춘선"
    else -> subwayId
}