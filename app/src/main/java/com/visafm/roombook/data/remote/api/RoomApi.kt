package com.visafm.roombook.data.remote.api

import com.visafm.roombook.data.model.Room
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// RoomAPI.kt
interface RoomApi {
    @GET("getRoomsList")
    fun getRoomList(): Call<List<Room>>

    @POST("orders")
    fun createOrder(@Body order: Room): Call<Room>

    // Define other API endpoints for orders
}
