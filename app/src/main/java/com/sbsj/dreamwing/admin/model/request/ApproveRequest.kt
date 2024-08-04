package com.sbsj.dreamwing.admin.model.request

/**
 * 봉사활동 승인 요청 데이터 클래스
 * @author 정은지
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04  	정은지       최초 생성
 * </pre>
 */
data class ApproveRequest(
    val volunteerId: Long,
    val userId: Long
)
