package com.visafm.roombook.data.remote.network

import com.visafm.roombook.data.remote.api.RoomApi
import com.visafm.roombook.data.remote.api.UserApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

// RetrofitClient.kt
object RetrofitClient {
    private var retrofit: Retrofit? = null
    private lateinit var baseUrl: String
    private lateinit var userSession: String

    fun init(baseUrl: String, userSession: String) {
        this.baseUrl = baseUrl
        this.userSession = userSession
    }

    fun getOkHttp(): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }

    fun getClient(): Retrofit {
        if (retrofit == null) {
            retrofit = Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(getOkHttp())
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
        }
        return retrofit!!
    }

    fun getRoomApi(): RoomApi {
        return getClient().create(RoomApi::class.java)
    }

    fun getUserApi(): UserApi {
        return getClient().create(UserApi::class.java)
    }
}