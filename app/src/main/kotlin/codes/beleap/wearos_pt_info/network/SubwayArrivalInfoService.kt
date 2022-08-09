package codes.beleap.wearos_pt_info.network

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .baseUrl("http://swopenapi.seoul.go.kr/api/subway/sample/json/realtimeStationArrival/")
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .build()

interface SubwayArrivalInfoService {
    @GET("0/{count}/{target}")
    suspend fun getSubwayArrivalInfo(
        @Path("count") count: Int,
        @Path("target") target: String,
    ): SubwayArrivalInfoResponse
}

object SubwayArrivalInfoApi {
    val retrofitService: SubwayArrivalInfoService by lazy { retrofit.create(SubwayArrivalInfoService::class.java) }
}