package com.sbsj.dreamwing.support.model.response

import com.google.gson.annotations.SerializedName
import com.sbsj.dreamwing.support.model.SupportDetailDTO

data class SupportDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SupportDetailDTO
)
