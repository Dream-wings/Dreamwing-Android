package com.sbsj.dreamwing.admin.model.request

/**
 * 봉사활동 인증 완료 처리 및 포인트 부여 요청 데이터 클래스
 * @author 정은지
 * @since 2024.08.05
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05   정은지        최초 생성
 */
data class AwardVolunteerPointRequest(
    val userId : Long,
    val volunteerId : Long,
    val activityType: Int,
    val activityTitle: String,
    val point: Int
)
