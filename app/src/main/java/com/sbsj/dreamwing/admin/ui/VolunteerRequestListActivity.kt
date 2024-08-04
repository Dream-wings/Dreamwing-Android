package com.sbsj.dreamwing.admin.ui

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestListResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityVolunteerRequestListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VolunteerRequestListActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerRequestListBinding
    private lateinit var volunteerRequestRecyclerViewAdapter: VolunteerRequestRecyclerViewAdapter
    private val requestList = mutableListOf<VolunteerRequestListResponse>()
    private var page = 0
    private val size = 10

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerRequestListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // RecyclerView 설정
        volunteerRequestRecyclerViewAdapter = VolunteerRequestRecyclerViewAdapter(requestList, this)
        binding.recyclerView.adapter = volunteerRequestRecyclerViewAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        // 데이터 불러오기
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    fetchRequestList()
                }
            }
        })

    }

    private fun fetchRequestList() {
        RetrofitClient.adminService.getVolunteerRequestList(page, size).enqueue(object :
            Callback<ApiResponse<List<VolunteerRequestListResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<VolunteerRequestListResponse>>>,
                response: Response<ApiResponse<List<VolunteerRequestListResponse>>>
            ) {
                if (response.isSuccessful) {
                    val requestData = response.body()?.data
                    Log.d("VolunteerRequestListActivity", "RequestList: $requestData")
                    requestData?.let {
                        requestList.addAll(it)
                        volunteerRequestRecyclerViewAdapter.notifyDataSetChanged()
                        page++
                    }
                } else {
                    Log.e("VolunteerRequestListActivity", "Response not successful")
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<List<VolunteerRequestListResponse>>>,
                t: Throwable
            ) {
                Log.e("VolunteerRequestListActivity", "Error: ${t.message}")
            }
        })
    }
}
