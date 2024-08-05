package com.sbsj.dreamwing.admin.model.response

import com.google.gson.annotations.SerializedName

data class VolunteerAdminListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<VolunteerAdminListDTO> // 변경된 부분
)