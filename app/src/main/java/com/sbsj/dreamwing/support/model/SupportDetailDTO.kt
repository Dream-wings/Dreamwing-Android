package com.sbsj.dreamwing.support.model

import com.google.gson.annotations.SerializedName

data class SupportDetailDTO(
    @SerializedName("supportId") val supportId: Long,
    @SerializedName("title") val title: String,
    @SerializedName("content") val content: String,
    @SerializedName("category") val category: Int,
    @SerializedName("goalPoint") val goalPoint: Int,
    @SerializedName("currentPoint") val currentPoint: Int,
    @SerializedName("recruitStartDate") val recruitStartDate: String,
    @SerializedName("recruitEndDate") val recruitEndDate: String,
    @SerializedName("imageUrl") val imageUrl: String?
)
