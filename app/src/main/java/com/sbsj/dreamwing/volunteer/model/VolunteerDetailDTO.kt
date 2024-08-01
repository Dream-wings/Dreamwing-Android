package com.sbsj.dreamwing.volunteer.model

import com.google.gson.annotations.SerializedName

data class VolunteerDetailDTO(
    @SerializedName("volunteerId") val volunteerId: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("type") val type: Int,
    @SerializedName("category") val category: Int,
    @SerializedName("volunteerStartDate") val volunteerStartDate: String?,
    @SerializedName("volunteerEndDate") val volunteerEndDate: String?,
    @SerializedName("address") val address: String?,
    @SerializedName("totalCount") val totalCount: Int,
    @SerializedName("status") val status: Int,
    @SerializedName("recruitStartDate") val recruitStartDate: String?,
    @SerializedName("recruitEndDate") val recruitEndDate: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("currentApplicantCount") val currentApplicantCount: Int,
    @SerializedName("latitude") val latitude: String?, // 서버와 일치하도록 String으로 설정
    @SerializedName("longitude") val longitude: String? // 서버와 일치하도록 String으로 설정
)

