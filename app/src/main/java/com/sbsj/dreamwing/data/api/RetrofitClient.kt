package com.sbsj.dreamwing.data.api

import com.sbsj.dreamwing.support.service.SupportService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://192.168.0.15:80"  // http://localhost:

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val instance: ApiService by lazy {
        retrofit.create(ApiService::class.java)
    }

    val supportService : SupportService by lazy {
        retrofit.create(SupportService::class.java)
    }
}