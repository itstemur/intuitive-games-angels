package com.visafm.roombook.data.repository.remote

import com.visafm.roombook.data.model.LoginRequest
import com.visafm.roombook.data.model.LoginResult
import com.visafm.roombook.data.model.NetworkResponse
import com.visafm.roombook.data.remote.api.UserApi
import com.visafm.roombook.data.repository.UserRepository
import io.reactivex.rxjava3.core.Single

class UserRemoteRepository(private val userApi: UserApi) : UserRepository {
    override fun login(username: String, password: String): Single<NetworkResponse<LoginResult>> {
        val request = LoginRequest(username, password)
        return userApi.login(request)
    }
}
