package com.sbsj.dreamwing.common.model;

data class ErrorResponse (
    val code: Int,
    val success: Boolean,
    val message: String
)