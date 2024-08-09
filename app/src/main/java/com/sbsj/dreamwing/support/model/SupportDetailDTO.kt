package com.sbsj.dreamwing.support.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 후원 상세 정보를 담는 데이터 전송 객체 (DTO)
 * @author 임재성
 * @since 2024.07.31
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   임재성        최초 생성
 */
data class SupportDetailDTO(
    @SerializedName("supportId") val supportId: Long,
    @SerializedName("goalPoint") val goalPoint: Int,
    @SerializedName("currentPoint") val currentPoint: Int,
    @SerializedName("title") val title: String?,
    @SerializedName("content") val content: String?,
    @SerializedName("startDate") val startDate: String?,
    @SerializedName("endDate") val endDate: String?,
    @SerializedName("imageUrl") val imageUrl: String?
)
