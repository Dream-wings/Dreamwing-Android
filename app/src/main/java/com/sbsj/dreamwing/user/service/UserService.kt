package com.sbsj.dreamwing.user.service
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.user.model.dto.SignUpRequestDTO
import com.sbsj.dreamwing.user.model.response.SignUpResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

/**
 * 사용자 서비스
 * @author 정은찬
 * @since 2024.08.01
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01   정은찬        최초 생성
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
}
