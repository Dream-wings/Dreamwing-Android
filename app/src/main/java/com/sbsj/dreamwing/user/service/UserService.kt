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
 * 사용자 서비스
 * @author 정은찬
 * @since 2024.08.01
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   정은찬        최초 생성
 * 2024.08.02   정은찬        아이디 중복 확인 기능 추가
 * 2024.08.03   정은찬         로그인 기능 추가
 */
interface UserService {
    @Multipart
    @POST("/user/signUp")
    fun signUp(
        @Part("loginId") loginId: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part imageFile: MultipartBody.Part?,  // Nullable type
        @Part("profileImageUrl") profileImageUrl: RequestBody?
    ): Call<SignUpResponse>

    @GET("/user/checkExistId")
    fun checkExistLoginId(
        @Query("loginId") loginId: String
    ): Call<CheckExistIdResponse>
    
    @POST("/user/login")
    fun login(
        @Body loginRequestDTO: LoginRequestDTO
    ): Call<Void>

    @GET("/user/getMyPageInfo")
    fun getMyPageInfo(
        @Header("Authorization") authHeader: String
    ): Call<ApiResponse<MyPageDTO>>

    @GET("/user/getPointList")
    fun getUserPointList(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiResponse<List<MyPointVO>>>

    @GET("/user/getSupportList")
    fun getUserSupportList(
        @Header("Authorization") authHeader: String,
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<ApiResponse<List<MySupportVO>>>

    @Multipart
    @POST("/user/update")
    fun update(
        @Header("Authorization") authHeader: String,
        @Part("loginId") loginId: RequestBody,
        @Part("password") password: RequestBody,
        @Part("name") name: RequestBody,
        @Part("phone") phone: RequestBody,
        @Part imageFile: MultipartBody.Part?,  // Nullable type
        @Part("profileImageUrl") profileImageUrl: RequestBody?
    ): Call<UpdateResponse>

    @POST("/user/logout")
    fun logout(
        @Header("Authorization") authHeader: String
    ): Call<Void>
}
