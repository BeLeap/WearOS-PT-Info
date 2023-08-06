package codes.beleap.wearos_pt_info.dto

import java.time.LocalDateTime

data class SubwayArrivalInfoResponse(
    val status: Int,
    val code: String,
    val message: String,
    val errorMessage: ErrorMessage,
    val realtimeArrivalList: List<SubwayArrivalInfo>,
)

data class ErrorMessage(
    val status: Int,
    val message: String,
)

data class SubwayArrivalInfo(
    val subwayId: String,
    val trainLineNm: String,
    var barvlDt: Long,
    val recptnDt: LocalDateTime,
    val arvlMsg2: String,
)

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