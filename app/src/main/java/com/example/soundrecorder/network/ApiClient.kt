package com.clabs.caddyapp.network

import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.Protocol
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.jackson.JacksonConverterFactory
import java.util.concurrent.TimeUnit

class ApiClient {
    val BASE_URL = "https://cricketstats.app/recorder-app/api/v1/" /* old api "http://18.222.228.117/recorderapp-backend/index.php/api/v1/"*/
    private var retrofit: Retrofit? = null

    fun getClient(context: Context): Retrofit? {
        if (retrofit == null) {

            val httpLoggingInterceptor = HttpLoggingInterceptor()
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)

            val client = OkHttpClient.Builder()
                .protocols(okhttp3.internal.Util.immutableList(Protocol.HTTP_1_1))
                .readTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .addInterceptor(httpLoggingInterceptor)
                .build()

            retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create()).build()
        }
        return retrofit
    }
}