package com.sbsj.dreamwing.support.model.response

import com.google.gson.annotations.SerializedName
import com.sbsj.dreamwing.support.model.SupportDetailDTO
/**
 * 후원 상세 조회 API의 응답을 나타내는 데이터 전송 객체 (DTO).
 * @author 임재성
 * @since 2024.08.04
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   임재성        최초 생성
 */
data class SupportDetailResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: SupportDetailDTO
)
