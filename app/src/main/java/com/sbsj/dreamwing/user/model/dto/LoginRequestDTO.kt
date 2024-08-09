package com.sbsj.dreamwing.user.model.dto

/**
 *  로그인 요청을 위한 데이터 전송 객체
 *
 * @author 정은찬
 * @since 2024.08.03
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은찬        최초 생성
 * </pre>
 */
data class LoginRequestDTO (
    val loginId: String,
    val password: String
)