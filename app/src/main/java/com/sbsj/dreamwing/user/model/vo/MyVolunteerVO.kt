package com.sbsj.dreamwing.user.model.vo

/**
 * 회원 봉사 활동 정보 데이터 클래스
 * @author 정은찬
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   정은찬        최초 생성
 * 2024.08.06   정은지        volunteerId 변수 추가
 * </pre>
 */
data class MyVolunteerVO(
    val volunteerId: Long,
    val title: String,
    val volunteerEndDate: String,
    val verified: Int
)