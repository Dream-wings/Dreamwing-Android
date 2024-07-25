package com.sbsj.dreamwing_android.data.api

import com.sbsj.dreamwing_android.data.model.UserInfo
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/test/user")
    fun getUserById(@Query("user_id") id: Int): Call<UserInfo>

    @GET("/users")
    fun getAllUsers(): Call<List<UserInfo>>
}