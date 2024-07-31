package com.sbsj.dreamwing.data.api

import com.sbsj.dreamwing.data.model.VolunteerListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    @GET("/volunteer/list")
    fun getVolunteerList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<VolunteerListResponse>
}
