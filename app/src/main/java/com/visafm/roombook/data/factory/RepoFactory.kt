package com.visafm.roombook.data.factory

import android.content.Context
import com.visafm.roombook.data.remote.network.RetrofitClient
import com.visafm.roombook.data.repository.SharedPreferencesRepository
import com.visafm.roombook.data.repository.UserRepository
import com.visafm.roombook.data.repository.remote.UserRemoteRepository
import com.visafm.roombook.data.repository.sharedpref.SharedPref

/**
 * a custom factory to use instead of DI
 */
object RepoFactory {
    private var userRemoteRepository: UserRemoteRepository? = null

    fun createSharedPref(context: Context): SharedPreferencesRepository {
        return SharedPref(
            sharedPreferences = context.getSharedPreferences(
                "roombook_shared_preferences",
                Context.MODE_PRIVATE
            )
        )
    }

    fun getUserRemoteRepository(): UserRepository{
        if(userRemoteRepository == null){
            userRemoteRepository = UserRemoteRepository(RetrofitClient.getUserApi())
        }

        return userRemoteRepository!!
    }
}