package com.sbsj.dreamwing.volunteer.service

import com.sbsj.dreamwing.admin.model.response.AdminVolunteerDTO
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.volunteer.model.PostApplyVolunteerRequestDTO
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
import com.sbsj.dreamwing.volunteer.model.response.VolunteerListResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.Part
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * 봉사활동 서비스 인터페이스
 * @author 임재성
 * @since 2024.08.01
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   임재성        최초 생성
 * 2024.08.01   임재성        봉사활동 리스트 조회 기능 추가
 * 2024.08.01   임재성        봉사활동 상세페이지 조회 기능 추가
 * 2024.08.02   임재성        봉사활동 신청하기 기능 추가
 * 2024.08.03   임재성        봉사활동 신청 상태 확인 기능 추가
 * 2024.08.03   임재성        봉사활동 신청 취소 기능 추가
 * 2024.08.03   임재성        봉사활동 신청 승인 상태 확인 기능 추가
 * 2024.08.03   정은지        봉사활동 인증 추가
 * 2024.08.04   임재성        봉사활동 모집중/모집완료 확인 기능 추가
 * 2024.08.05   임재성        관리자 페이지 봉사활동 리스트 조회 기능 추가
 * 2024.08.05   임재성        관리자 페이지 봉사활동 수정 기능 추가
 * 2024.08.05   임재성        관리자 페이지 봉사활동 취소 기능 추가
 * 2024.08.06   임재성        헤더에 토큰 삽입
 */
interface VolunteerService {

    /**
     * 봉사활동 상세페이지 조회
     * @author 임재성
     * @since 2024.08.01
     * @version 1.0
     */
    @GET("/volunteer/detail")
    fun getVolunteerDetail( @Header("Authorization") authHeader: String, @Query("volunteerId") volunteerId: Long): Call<VolunteerDetailResponse>

    /**
     * 봉사활동 신청하기
     * @author 임재성
     * @since 2024.08.02
     * @version 1.0
     */
    @POST("/volunteer/apply")
    fun applyForVolunteer(@Header("Authorization") authHeader: String,@Body request: PostApplyVolunteerRequestDTO): Call<ApiResponse<Unit>>

    /**
     * 봉사활동 신청 여부 확인
     * @author 임재성
     * @since 2024.08.03
     * @version 1.0
     */
    @GET("/volunteer/check-application")
    fun checkApplicationStatus(
        @Header("Authorization") authHeader: String,
        @Query("volunteerId") volunteerId: Long

    ): Call<ApiResponse<Boolean>>

    /**
     * 봉사활동 신청 상태 확인
     * @author 임재성
     * @since 2024.08.03
     * @version 1.0
     */
    @POST("/volunteer/check-application")
        fun checkIfAlreadyApplied(  @Header("Authorization") authHeader: String,
                                    @Body request: PostApplyVolunteerRequestDTO): Call<ApiResponse<Boolean>>

    /**
     * 봉사활동 신청 취소
     * @author 임재성
     * @since 2024.08.03
     * @version 1.0
     */
    @POST("/volunteer/cancel-application")
    fun cancelApplication(@Header("Authorization") authHeader: String,@Body request: PostApplyVolunteerRequestDTO): Call<ApiResponse<Unit>>

    /**
     * 봉사활동 신청 승인 상태 확인
     * @author 임재성
     * @since 2024.08.03
     * @version 1.0
     */
    @GET("/volunteer/check-status")
    fun checkStatus(
        @Header("Authorization") authHeader: String,
        @Query("volunteerId") volunteerId: Long,

    ): Call<ApiResponse<Int>>

    /**
     * 봉사활동 리스트 조회
     * @author 임재성
     * @since 2024.08.01
     * @version 1.0
     */
    @GET("/volunteer/list")
    fun getVolunteerListWithStatus(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Query("status") status: Int, // Add status parameter for filtering
        @Query("type") type: Int? // Add type parameter for filtering
    ): Call<VolunteerListResponse>

    /**
     * 봉사활동 인증하기
     * @author 정은지
     * @since 2024.08.04
     * @version 1.0
     */
    @Multipart
    @PATCH("/volunteer/certification")
    fun certificationVolunteer(@Header("Authorization") authHeader: String,
                               @Part("request") request: RequestBody,
                               @Part imageFile: MultipartBody.Part?) : Call<ApiResponse<Void>>

    /**
     * 관리자 페이지 봉사활동 리스트 조회
     * @author 임재성
     * @since 2024.08.05
     * @version 1.0
     */
    @GET("/admin/volunteer/adminList")
    fun getVolunteerList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<VolunteerAdminListResponse>


    /**
     * 관리자 페이지 봉사활동 수정
     * @author 임재성
     * @since 2024.08.05
     * @version 1.0
     */
    @PUT("/admin/volunteer/update/{id}")
    fun updateVolunteer(
        @Path("id") volunteerId: Long,
        @Body request: VolunteerDetailDTO
    ): Call<ApiResponse<Void>>

    /**
     * 관리자 페이지 봉사활동 삭제
     * @author 임재성
     * @since 2024.08.05
     * @version 1.0
     */
    @DELETE("/admin/volunteer/delete/{volunteerId}")
    fun deleteVolunteer(
        @Path("volunteerId") volunteerId: Long
    ): Call<ApiResponse<Void>>

    /**
     * 관리자 페이지 봉사활동 생성
     * @author 임재성
     * @since 2024.08.05
     * @version 1.0
     */
    @POST("/admin/volunteer/create")
    fun createVolunteer(
        @Body request: AdminVolunteerDTO
    ): Call<ApiResponse<Void>>
}




