package com.sbsj.dreamwing.user.service
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.mission.model.response.QuizResponse
import com.sbsj.dreamwing.user.model.dto.LoginRequestDTO
import com.sbsj.dreamwing.user.model.dto.MyPageDTO
import com.sbsj.dreamwing.user.model.response.CheckExistIdResponse
import com.sbsj.dreamwing.user.model.response.SignUpResponse
import com.sbsj.dreamwing.user.model.response.UpdateResponse
import com.sbsj.dreamwing.user.model.response.UserInfoResponse
import com.sbsj.dreamwing.user.model.vo.MyPointVO
import com.sbsj.dreamwing.user.model.vo.MySupportVO
import com.sbsj.dreamwing.user.model.vo.MyVolunteerVO
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Query

/**
 * 관리자 서비스 인터페이스
 * @author 정은찬
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   정은찬        최초 생성
 * 2024.08.02   정은찬        아이디 존재 여부 확인 추가
 * 2024.08.03   정은찬        로그인 추가
 * 2024.08.04   정은찬        마이페이지 정보 조회, 포인트 내역, 후원 내역, 봉사 활동 내역 조회 추가 
 * 2024.08.05   정은찬        회원 정보 업데이트, 로그아웃 추가
 * </pre>
 */
interface UserService {
    /**
     * 회원가입
     * @author 정은찬
     * @since 2024.08.01
     * @version 1.0
     */
    @Multipart
    @POST("/user/signUp")
    fun signUp(
        @Part("loginId") loginId: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part imageFile: MultipartBody.Part?,
        @Part("profileImageUrl") profileImageUrl: RequestBody?
    ): Call<SignUpResponse>

    /**
     * 로그인 아이디 존재 여부 확인
     * @author 정은찬
     * @since 2024.08.02
     * @version 1.0
     */
    @GET("/user/checkExistId")
    fun checkExistLoginId(
        @Query("loginId") loginId: String
    ): Call<CheckExistIdResponse>

    /**
     * 로그인
     * @author 정은찬
     * @since 2024.08.03
     * @version 1.0
     */
    @POST("/user/login")
    fun login(
        @Body loginRequestDTO: LoginRequestDTO
    ): Call<Void>

    /**
     * 마이페이지 정보 조회
     * @author 정은찬
     * @since 2024.08.04
     * @version 1.0
     */
    @GET("/user/auth/getMyPageInfo")
    fun getMyPageInfo(
        @Header("Authorization") authHeader: String
    ): Call<ApiResponse<MyPageDTO>>

    /**
     * 회원 포인트 내역 조회
     * @author 정은찬
     * @since 2024.08.04
     * @version 1.0
     */
    @GET("/user/auth/getPointList")
    fun getUserPointList(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiResponse<List<MyPointVO>>>

    /**
     * 회원 후원 내역 조회
     * @author 정은찬
     * @since 2024.08.04
     * @version 1.0
     */
    @GET("/user/auth/getSupportList")
    fun getUserSupportList(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiResponse<List<MySupportVO>>>

    /**
     * 회원 봉사 활동 내역 조회
     * @author 정은찬
     * @since 2024.08.04
     * @version 1.0
     */
    @GET("/user/auth/getVolunteerList")
    fun getUserVolunteerList(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiResponse<List<MyVolunteerVO>>>

    /**
     * 회원 정보 업데이트
     * @author 정은찬
     * @since 2024.08.05
     * @version 1.0
     */
    @Multipart
    @POST("/user/auth/update")
    fun update(
        @Header("Authorization") authHeader: String,
        @Part("loginId") loginId: RequestBody?,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part imageFile: MultipartBody.Part?,  // Nullable type
        @Part("profileImageUrl") profileImageUrl: RequestBody?
    ): Call<UpdateResponse>

    /**
     * 로그아웃
     * @author 정은찬
     * @since 2024.08.05
     * @version 1.0
     */
    @POST("/user/auth/logout")
    fun logout(
        @Header("Authorization") authHeader: String
    ): Call<Void>
}
