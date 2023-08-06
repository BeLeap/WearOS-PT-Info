package codes.beleap.wearos_pt_info.repository

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit

object RetrofitHelper {
    fun debugHttpClient(builder: Retrofit.Builder, isDebugMode: Boolean): Retrofit.Builder {
        val httpClient = OkHttpClient.Builder()
            .apply {
                val loggingInterceptor = HttpLoggingInterceptor()

                if (isDebugMode) {
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
                } else {
                    loggingInterceptor.level = HttpLoggingInterceptor.Level.BASIC
                }

                addInterceptor(loggingInterceptor)
            }
            .build()

        return builder.client(httpClient)
    }
}