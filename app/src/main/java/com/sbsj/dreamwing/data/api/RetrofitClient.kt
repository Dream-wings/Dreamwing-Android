package com.sbsj.dreamwing.data.api

import com.sbsj.dreamwing.BuildConfig
import com.sbsj.dreamwing.admin.service.AdminService
import com.sbsj.dreamwing.mission.service.MissionService
import com.sbsj.dreamwing.support.service.SupportService
import com.sbsj.dreamwing.user.service.UserService
import com.sbsj.dreamwing.volunteer.service.VolunteerService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = BuildConfig.BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY // BODY, HEADERS, BASIC 등 선택 가능
    }

    private val httpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()
    val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val supportService: SupportService by lazy {
        retrofit.create(SupportService::class.java)
    }

    val userService: UserService by lazy {
        retrofit.create(UserService::class.java)
    }

    val volunteerService: VolunteerService by lazy {
        retrofit.create(VolunteerService::class.java)
    }

    val missionService: MissionService by lazy {
        retrofit.create(MissionService::class.java)
    }

    val adminService: AdminService by lazy {
        retrofit.create(AdminService::class.java)
    }
}