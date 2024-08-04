package com.sbsj.dreamwing.support.model.response

import com.google.gson.annotations.SerializedName

data class GetSupportListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<SupportListResponse>
)
