package com.sbsj.dreamwing.user.model.response

/**
 * 아이디 중복 확인 요청에 대한 응답 데이터 클래스
 *
 * @author 정은찬
 * @since 2024.08.02
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은찬        최초 생성
 * </pre>
 */
data class CheckExistIdResponse (
    val success: Boolean,
    val data: Boolean
)