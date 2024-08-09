package com.sbsj.dreamwing.volunteer.model

import com.google.gson.annotations.SerializedName
/**
 * 봉사활동 리스트 조회 요청 데이터 클래스
 * @author 임재성
 * @since 2024.08.03
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.07.31   임재성        최초 생성
 */
data class VolunteerListDTO(

    @SerializedName("volunteerId") val volunteerId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("type") val type: Int, // 수정
    @SerializedName("category") val category: Int, // 수정
    @SerializedName("volunteerStartDate") val volunteerStartDate: String,
    @SerializedName("volunteerEndDate") val volunteerEndDate: String,
    @SerializedName("address") val address: String,
    @SerializedName("status") val status: Int, // 수정
    @SerializedName("imageUrl") val imageUrl: String
)
