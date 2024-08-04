package com.sbsj.dreamwing.volunteer.model.request

import okhttp3.MultipartBody

/**
 * 봉사활동 인증 요청 데이터 클래스
 * @author 정은지
 * @since 2024.08.03
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은지        최초 생성
 */
data class CertificationVolunteerRequest(
    val userId: Long,
    val volunteerId: Long,
    val imageUrl: String?,
    val imageFile: MultipartBody.Part?
)