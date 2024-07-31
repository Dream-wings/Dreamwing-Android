package com.sbsj.dreamwing.data.model

import com.google.gson.annotations.SerializedName

data class VolunteerListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<VolunteerListDTO>
)
