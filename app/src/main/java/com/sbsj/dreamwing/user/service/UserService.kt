package com.sbsj.dreamwing.user.service
import com.sbsj.dreamwing.user.model.response.CheckExistIdResponse
import com.sbsj.dreamwing.user.model.response.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.GET
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
 * 2025.08.02   정은찬        아이디 중복 확인 함수
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
}
