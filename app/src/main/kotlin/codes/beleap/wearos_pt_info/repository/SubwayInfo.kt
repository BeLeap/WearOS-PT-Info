package codes.beleap.wearos_pt_info.repository

import codes.beleap.wearos_pt_info.dto.SubwayArrivalInfoResponse
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

interface SubwayInfo {
    @GET("{apiKey}/json/realtimeStationArrival/0/{count}/{target}")
    suspend fun getRealtimeArrivalInfo(
        @Path("apiKey") apiKey: String,
        @Path("count") count: Int,
        @Path("target") target: String,
    ): SubwayArrivalInfoResponse

    companion object {
        fun buildClient(isDebugMode: Boolean): SubwayInfo {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://swopenapi.seoul.go.kr/api/subway/")
                .addConverterFactory(GsonConverterFactory.create())
                .apply {
                    RetrofitHelper.debugHttpClient(this, isDebugMode)
                }
                .build()

            return retrofit.create(SubwayInfo::class.java)
        }
    }
}