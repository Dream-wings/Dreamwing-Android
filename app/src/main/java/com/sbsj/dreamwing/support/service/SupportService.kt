package com.sbsj.dreamwing.support.service

import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.support.model.response.GetSupportListResponse
import com.sbsj.dreamwing.support.model.response.SupportListResponse
import com.sbsj.dreamwing.support.model.response.TotalSupportResponse
import com.sbsj.dreamwing.support.model.response.SupportDetailResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 후원 서비스 인터페이스
 * @author 정은지
 * @since 2024.07.31
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.07.31   정은지        최초 생성
 */
interface SupportService {

    /**
     * 총 후원 횟수, 금액 조회
     * @author 정은지
     * @since 2024.07.31
     * @version 1.0
     */
    @GET("/support/total")
    fun getTotalSupport(): Call<ApiResponse<TotalSupportResponse>>

    /**
     * 후원 리스트 조회
     * @author 정은지
     * @since 2024.07.31
     * @version 1.0
     */
    @GET("/support/list/5")
    fun getSupportList(): Call<ApiResponse<List<SupportListResponse>>>

    @GET("/support/list")
    fun getSupportList(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: Int,     // 0 for 모금중, 1 for 모금완료
        @Query("category") category: Int  // 1 for 교육, 2 for 물품, etc.
    ): Call<GetSupportListResponse> // Ensure the correct return type

    @GET("/support/detail")
    fun getSupportDetail(@Query("supportId") supportId: Long): Call<SupportDetailResponse>

    @POST("/support/donate")
    fun donateForSupport(
        @Header("Authorization") authHeader: String,
        @Query("supportId") supportId: Long,
        @Query("amount") amount: Int,
        @Query("activityTitle") title: String,
        @Query("activityType") type: Int
    ): Call<ApiResponse<Unit>>

}
