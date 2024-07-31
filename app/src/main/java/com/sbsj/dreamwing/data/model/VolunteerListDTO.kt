package com.sbsj.dreamwing.data.model

import com.google.gson.annotations.SerializedName

data class VolunteerListDTO(
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: Int, // 수정
    @SerializedName("category") val category: Int, // 수정
    @SerializedName("volunteerStartDate") val volunteerStartDate: String,
    @SerializedName("volunteerEndDate") val volunteerEndDate: String,
    @SerializedName("address") val address: String,
    @SerializedName("status") val status: Int, // 수정
    @SerializedName("imageUrl") val imageUrl: String
)
