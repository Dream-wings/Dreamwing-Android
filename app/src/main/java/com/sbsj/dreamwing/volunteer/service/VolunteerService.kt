package com.sbsj.dreamwing.volunteer.service

import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
import com.sbsj.dreamwing.volunteer.model.response.VolunteerListResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface VolunteerService {

    @GET("/volunteer/list")
    fun getVolunteerList(
        @Query("page") page: Int,
        @Query("size") size: Int
    ): Call<VolunteerListResponse>

    @GET("/volunteer/detail")
    fun getVolunteerDetail(@Query("volunteerId") volunteerId: Long): Call<VolunteerDetailResponse>


}
