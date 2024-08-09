package com.sbsj.dreamwing.common.model;

/**
 * API 에러 응답 모델
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
data class ErrorResponse (
    val code: Int,
    val success: Boolean,
    val message: String
)