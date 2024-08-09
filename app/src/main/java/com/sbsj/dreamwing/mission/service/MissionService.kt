package com.sbsj.dreamwing.mission.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.mission.model.request.AwardPointRequest
import com.sbsj.dreamwing.mission.model.response.QuizResponse
import com.sbsj.dreamwing.support.model.response.TotalSupportResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST

/**
 * 미션 서비스 인터페이스
 * @author 정은지
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은지        최초 생성
 */
interface MissionService {

    /**
     * 데일리 퀴즈 퀴즈 조회
     * @author 정은지
     * @since 2024.08.02
     * @version 1.0
     */
    @GET("/mission/quiz")
    fun getDailyQuiz(): Call<ApiResponse<QuizResponse>>

    /**
     * 포인트 지급
     * @author 정은지
     * @since 2024.08.02
     * @version 1.0
     */
    @POST("/mission/point")
    fun awardPoints(@Header("Authorization") authHeader: String,
                    @Body request : AwardPointRequest): Call<ApiResponse<Any>>
}