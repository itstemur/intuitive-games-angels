package com.visafm.roombook.data.model

import com.google.gson.annotations.SerializedName

data class NetworkResponse<T>(
    @SerializedName("ResultCode")
    val resultCode: String,
    @SerializedName("ResultObject")
    val resultObject: T,
    @SerializedName("ResultMessage")
    val resultMessage: String
)
