package com.sbsj.dreamwing.user.model.dto
import okhttp3.MultipartBody

data class SignUpRequestDTO(
    val loginId: String,
    val password: String,
    val name: String,
    val phone: String,
    val imageFile: MultipartBody.Part?,
    val profileImageUrl: String?
)