package com.sbsj.dreamwing.admin.model.response

import com.google.gson.annotations.SerializedName

/**
 * 관리자 페이지에서 봉사활동 목록을 나타내는 데이터 전송 객체 (DTO)
 * @author 임재성
 * @since 2024.08.05
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05   임재성        최초 생성
 */
data class VolunteerAdminListDTO(
    @SerializedName("volunteerId") val volunteerId: Long,
    @SerializedName("title") val title: String?,
    @SerializedName("type") val type: Int,
    @SerializedName("currentCount") val currentCount: Int,
    @SerializedName("totalCount") val totalCount: Int
)