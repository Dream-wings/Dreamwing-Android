package com.sbsj.dreamwing.admin.model.response

/**
 * 봉사활동 신청 대기 리스트 응답 데이터 클래스
 * @author 정은지
 * @since 2024.08.04
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   정은지        최초 생성
 */
data class VolunteerRequestListResponse(
    val volunteerId : Long,
    val type : Int,
    val title : String,
    val loginId : String
)