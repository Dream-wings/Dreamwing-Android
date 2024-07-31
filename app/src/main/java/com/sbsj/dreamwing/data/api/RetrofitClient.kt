package com.sbsj.dreamwing.data.api

import com.sbsj.dreamwing.support.service.SupportService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2"  // http://localhost:

    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()


    val instance: ApiService by lazy {
//        val retrofit = Retrofit.Builder()
//            .baseUrl(BASE_URL)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
        retrofit.create(ApiService::class.java)
    }

    val supportService: SupportService by lazy {
        retrofit.create(SupportService::class.java)
    }
}