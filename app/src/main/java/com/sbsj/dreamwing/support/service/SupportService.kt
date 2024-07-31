package com.sbsj.dreamwing.support.service

import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.support.model.response.TotalSupportResponse
import retrofit2.Call
import retrofit2.http.GET

/**
 * 후원 서비스
 * @author 정은지
 * @since 2024.07.31
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.07.31   정은지        최초 생성
 */
interface SupportService {

    @GET("/support/total")
    fun getTotalSupport(): Call<ApiResponse<TotalSupportResponse>>

}