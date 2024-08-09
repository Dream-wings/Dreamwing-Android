package com.sbsj.dreamwing.volunteer.model.response

import com.google.gson.annotations.SerializedName
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO


data class VolunteerDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: VolunteerDetailDTO
)
