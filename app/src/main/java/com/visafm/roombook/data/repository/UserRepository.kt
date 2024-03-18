package com.visafm.roombook.data.repository

import com.visafm.roombook.data.model.LoginResult
import com.visafm.roombook.data.model.NetworkResponse
import io.reactivex.rxjava3.core.Single

interface UserRepository {
    fun login(username: String, password: String): Single<NetworkResponse<LoginResult>>
}
