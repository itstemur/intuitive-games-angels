package com.visafm.roombook.data.repository

interface SharedPreferencesRepository {
    fun getString(key: String, defaultValue: String): String
    fun putString(key: String, value: String)
    // Define other methods for different data types as needed
}
