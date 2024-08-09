package com.sbsj.dreamwing.support.model.response

/**
 * 총 후원 횟수, 금액 응답 모델
 * @author 정은지
 * @since 2024.07.31
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.07.31   정은지        최초 생성
 */
data class TotalSupportResponse (
    val totalCount: Int,
    val totalPoints: Int
)