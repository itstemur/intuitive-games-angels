package com.visafm.roombook.data.remote.api

import com.visafm.roombook.data.model.LoginRequest
import com.visafm.roombook.data.model.LoginResult
import com.visafm.roombook.data.model.NetworkResponse
import io.reactivex.rxjava3.core.Single
import retrofit2.http.Body
import retrofit2.http.POST

interface UserApi {
    @POST("Account/ApplicantLogin")
    fun login(@Body request: LoginRequest): Single<NetworkResponse<LoginResult>>
}
