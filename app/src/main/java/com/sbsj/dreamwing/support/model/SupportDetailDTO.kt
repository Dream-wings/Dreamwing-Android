package com.sbsj.dreamwing.support.model

import com.google.gson.annotations.SerializedName
import java.util.Date

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
