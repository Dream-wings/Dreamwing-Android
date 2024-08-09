package com.sbsj.dreamwing.common.model

/**
 * API 응답 모델
 * @author 정은지
 * @since 2024.07.31
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.07.31  	정은지       최초 생성
 * </pre>
 */
data class ApiResponse<T>(
    val code: Int,
    val success: Boolean,
    val message: String,
    val data: T
)
