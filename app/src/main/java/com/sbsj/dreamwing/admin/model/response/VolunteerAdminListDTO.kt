package com.sbsj.dreamwing.admin.model.response

import com.google.gson.annotations.SerializedName

data class VolunteerAdminListDTO(
    @SerializedName("volunteerId") val volunteerId: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("type") val type: Int,
    @SerializedName("currentCount") val currentCount: Int,
    @SerializedName("totalCount") val totalCount: Int
)