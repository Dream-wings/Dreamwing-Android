package com.sbsj.dreamwing.common.model

data class ApiResponse<T>(
    val code: Int,
    val success: Boolean,
    val message: String,
    val data: T
)
