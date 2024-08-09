package com.sbsj.dreamwing.volunteer.model

import com.google.gson.annotations.SerializedName


/**
 * 봉사활동 상세페이지 조회 요청 데이터 클래스
 * @author 임재성
 * @since 2024.08.01
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   임재성        최초 생성
 */
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
    @SerializedName("latitude") val latitude: Double?,
    @SerializedName("longitude") val longitude: Double?,
    @SerializedName("currentApplicantCount") val currentApplicantCount: Int
)

