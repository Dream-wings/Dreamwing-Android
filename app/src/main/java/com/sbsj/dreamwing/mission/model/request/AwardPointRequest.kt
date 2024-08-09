package com.sbsj.dreamwing.mission.model.request

/**
 * 포인트 부여 요청 DTO
 * @author 정은지
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은지        최초 생성
 */
data class AwardPointRequest(
    val activityType: Int,
    val activityTitle: String,
    val point: Int
)