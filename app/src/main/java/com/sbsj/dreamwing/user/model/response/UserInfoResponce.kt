package com.sbsj.dreamwing.user.model.response

/**
 * 사용자 정보 요청에 대한 응답 데이터 클래스
 *
 * @author 정은찬
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   정은찬        최초 생성
 * </pre>
 */
data class UserInfoResponse(
    val name: String,
    val phone: String,
    val profileImageUrl: String,
    val totalPoint : Int
)