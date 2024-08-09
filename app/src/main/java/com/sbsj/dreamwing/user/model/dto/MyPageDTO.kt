package com.sbsj.dreamwing.user.model.dto

/**
 *  마이페이지 요청을 위한 데이터 전송 객체 (DTO)
 *
 * @author 정은찬
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은찬        최초 생성
 * </pre>
 */
data class MyPageDTO(
    val name: String,
    val phone: String,
    val profileImageUrl: String,
    val totalPoint: Int,
    val totalSupportPoint: Int
)