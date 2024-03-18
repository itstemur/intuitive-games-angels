package com.visafm.roombook.data.model

import com.google.gson.annotations.SerializedName

data class LoginResult(
    @SerializedName("SessionUserID")
    val sessionUserID: String,
    @SerializedName("SessionApplicationID")
    val sessionApplicationID: String
)