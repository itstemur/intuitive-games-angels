package com.visafm.roombook.data.repository.sharedpref

import android.content.Context
import android.content.SharedPreferences
import com.visafm.roombook.data.repository.SharedPreferencesRepository

class SharedPref(private val sharedPreferences: SharedPreferences) :
    SharedPreferencesRepository {
    override fun getString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    override fun putString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    companion object {

    }
}
