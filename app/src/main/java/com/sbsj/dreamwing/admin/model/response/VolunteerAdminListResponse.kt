package com.sbsj.dreamwing.admin.model.response

import com.google.gson.annotations.SerializedName
/**
 * 관리자 페이지에서 봉사활동 목록 API의 응답을 나타내는 데이터 전송 객체 (DTO)
 * @author 임재성
 * @since 2024.08.05
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05   임재성        최초 생성
 * 2024.08.05   임재성        봉사활동 목록 데이터 필드 추가
 */
data class VolunteerAdminListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<VolunteerAdminListDTO> // 변경된 부분
)