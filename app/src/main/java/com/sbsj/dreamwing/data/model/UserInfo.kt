package com.sbsj.dreamwing.data.model

import com.google.gson.annotations.SerializedName

data class UserInfo(
    //UserDTO 같은 것입니다.
    @SerializedName("user_id") val userId: Int,
    @SerializedName("login_id") val loginId: String,
    @SerializedName("password") val password: String,
    @SerializedName("name") val name: String,
    @SerializedName("phone") val phone: String,
    @SerializedName("total_point") val totalPoint: Int,
    @SerializedName("withdraw") val withdraw: Int,
    @SerializedName("profile_image_url") val profileImageUrl: String,
    @SerializedName("created_date") val createdDate: String
)