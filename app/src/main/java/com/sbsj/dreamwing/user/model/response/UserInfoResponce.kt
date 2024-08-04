package com.sbsj.dreamwing.user.model.response

data class UserInfoResponse(
    val name: String,
    val phone: String,
    val profileImageUrl: String,
    val totalPoint : Int
)