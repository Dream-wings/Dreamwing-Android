package com.sbsj.dreamwing.user.model.vo

/**
 * 회원 후원 정보 데이터 클래스
 * @author 정은찬
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   정은찬        최초 생성
 * </pre>
 */
data class MySupportVO(
    val title: String,
    val point: Int,
    val createdDate: String
)
