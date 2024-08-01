package com.sbsj.dreamwing.data.api

import com.sbsj.dreamwing.BuildConfig
import com.sbsj.dreamwing.support.service.SupportService
import com.sbsj.dreamwing.volunteer.service.VolunteerService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

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

    // VolunteerService 인스턴스
    val volunteerService: VolunteerService by lazy {
        retrofit.create(VolunteerService::class.java)
    }
}