package com.sbsj.dreamwing.admin.service

import com.sbsj.dreamwing.admin.model.request.ApproveRequest
import com.sbsj.dreamwing.admin.model.request.AwardVolunteerPointRequest
import com.sbsj.dreamwing.admin.model.response.VolunteerCertificationDetailResponse
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestDetailResponse
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestListResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * 관리자 서비스 인터페이스
 * @author 정은지
 * @since 2024.08.04
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   정은지        최초 생성
 */
interface AdminService {

    /**
     * 봉사활동 신청 대기 목록
     * @author 정은지
     * @since 2024.08.04
     * @version 1.0
     */
    @GET("/admin/volunteer/request/list")
    fun getVolunteerRequestList(@Query("page") page: Int,
                                @Query("size") size: Int) : Call<ApiResponse<List<VolunteerRequestListResponse>>>

    /**
     * 봉사활동 신청 상세
     * @author 정은지
     * @since 2024.08.04
     * @version 1.0
     */
    @GET("/admin/volunteer/request")
    fun getVolunteerRequestDetail(@Query("volunteerId") volunteerId: Long,
                                  @Query("userId") userId: Long) : Call<ApiResponse<VolunteerRequestDetailResponse>>

    /**
     * 봉사활동 신청 승인
     * @author 정은지
     * @since 2024.08.04
     * @version 1.0
     */
    @PATCH("/admin/volunteer/approve")
    fun approveVolunteerRequest(@Body request: ApproveRequest) : Call<ApiResponse<Void>>

    /**
     * 봉사활동 인증 대기 목록
     * @author 정은지
     * @since 2024.08.05
     * @version 1.0
     */
    @GET("/admin/volunteer/certification/list")
    fun getVolunteerCertificationList(@Query("page") page: Int,
                                      @Query("size") size: Int) : Call<ApiResponse<List<VolunteerRequestListResponse>>>

    /**
     * 봉사활동 인증 대기 상세
     * @author 정은지
     * @since 2024.08.05
     * @version 1.0
     */
    @GET("/admin/volunteer/certification")
    fun getVolunteerCertificationDetail(@Query("volunteerId") volunteerId: Long,
                                        @Query("userId") userId: Long) : Call<ApiResponse<VolunteerCertificationDetailResponse>>

    /**
     * 봉사활동 인증 처리 및 포인트 부여
     * @author 정은지
     * @since 2024.08.05
     * @version 1.0
     */
    @POST("/admin/volunteer/point")
    fun awardVolunteerPoints(@Body request : AwardVolunteerPointRequest): Call<ApiResponse<Void>>
}