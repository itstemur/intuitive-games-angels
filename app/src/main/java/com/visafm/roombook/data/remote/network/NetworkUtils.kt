package com.visafm.roombook.data.remote.network

import android.content.Context
import android.net.ConnectivityManager

// NetworkUtils.kt
object NetworkUtils {
    fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as? ConnectivityManager
        connectivityManager?.let {
            val networkInfo = it.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
        return false
    }
}
