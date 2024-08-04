package com.sbsj.dreamwing.support.model.response

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 후원 리스트 응답 모델
 * @author 정은지
 * @since 2024.08.01
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   정은지        최초 생성
 */
data class SupportListResponse (
    @SerializedName("supportId") val supportId: Long,
    @SerializedName("goalPoint") val goalPoint: Int,
    @SerializedName("currentPoint") val currentPoint: Int,
    @SerializedName("title") val title: String,
    @SerializedName("endDate") val endDate: Date,
    @SerializedName("imageUrl") val imageUrl: String,
    @SerializedName("dday") val dday: String
)