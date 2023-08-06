@file:Suppress("DEPRECATION")

package codes.beleap.wearos_pt_info.repository

import codes.beleap.wearos_pt_info.dto.BusArrivalInfoResponse
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface BusStationInfo {
    @GET("getStationByUid")
    suspend fun getStationByUid(
        @Query("serviceKey") apiKey: String,
        @Query("arsId") targetBusStationId: String,
    ): BusArrivalInfoResponse

    companion object {
        fun buildClient(isDebugMode: Boolean): BusStationInfo {
            val retrofit = Retrofit.Builder()
                .baseUrl("http://ws.bus.go.kr/api/rest/stationinfo/")
                .addConverterFactory(SimpleXmlConverterFactory.create())
                .apply {
                    RetrofitHelper.debugHttpClient(this, isDebugMode)
                }
                .build()

            return retrofit.create(BusStationInfo::class.java)
        }
    }
}