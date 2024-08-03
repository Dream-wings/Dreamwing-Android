package com.sbsj.dreamwing.volunteer.service

import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.volunteer.model.PostApplyVolunteerRequestDTO
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
import com.sbsj.dreamwing.volunteer.model.response.VolunteerListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 봉사활동 서비스 인터페이스
 * @author
 * @since 2024.08.03
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은지        봉사활동 인증 추가
 */
interface VolunteerService {

//    @GET("/volunteer/list")
//    fun getVolunteerList(
//        @Query("page") page: Int,
//        @Query("size") size: Int
//    ): Call<VolunteerListResponse>

    @GET("/volunteer/detail")
    fun getVolunteerDetail(@Query("volunteerId") volunteerId: Long): Call<VolunteerDetailResponse>

    @POST("/volunteer/apply")
    fun applyForVolunteer(@Body request: PostApplyVolunteerRequestDTO): Call<ApiResponse<Unit>>

    @GET("/volunteer/check-application")
    fun checkApplicationStatus(
        @Query("volunteerId") volunteerId: Long,
        @Query("userId") userId: Long
    ): Call<ApiResponse<Boolean>>

    @POST("/volunteer/check-application")
        fun checkIfAlreadyApplied(@Body request: PostApplyVolunteerRequestDTO): Call<ApiResponse<Boolean>>

    // New method for canceling application
    @POST("/volunteer/cancel-application")
    fun cancelApplication(@Body request: PostApplyVolunteerRequestDTO): Call<ApiResponse<Unit>>


    @GET("/volunteer/check-status")
    fun checkStatus(
        @Query("volunteerId") volunteerId: Long,
        @Query("userId") userId: Long
    ): Call<ApiResponse<Int>>


    @GET("/volunteer/list")
    fun getVolunteerListWithStatus(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: Int, // Add status parameter for filtering
        @Query("type") type: Int? // Add type parameter for filtering
    ): Call<VolunteerListResponse>

    /**
     * @author 정은지
     * @since 2024.08.04
     */
    @Multipart
    @PATCH("/volunteer/certification")
    fun certificationVolunteer(@Part("request") request: RequestBody,
                               @Part imageFile: MultipartBody.Part?) : Call<ApiResponse<Void>>
}




