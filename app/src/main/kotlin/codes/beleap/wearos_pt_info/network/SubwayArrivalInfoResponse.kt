package codes.beleap.wearos_pt_info.network

data class SubwayArrivalInfoResponse(
    val errorMessage: ErrorMessage,
)

data class ErrorMessage(
    val status: Int,
    val message: String,
)