package com.sbsj.dreamwing.support.model.response

import com.google.gson.annotations.SerializedName
/**
 * 후원 리스트 조회 API의 응답을 나타내는 데이터 전송 객체 (DTO)
 * @author 임재성
 * @since 2024.08.01
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   임재성        최초 생성
 */
data class GetSupportListResponse(
    @SerializedName("code") val code: Int,
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: List<SupportListResponse>
)
