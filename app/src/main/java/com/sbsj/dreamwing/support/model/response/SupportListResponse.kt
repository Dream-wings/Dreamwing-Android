package com.sbsj.dreamwing.support.model.response

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
    val supportId: Long,
    val goalPoint: Int,
    val currentPoint: Int,
    val title: String,
    val endDate: Date,
    val imageUrl: String,
    val dday: String
)