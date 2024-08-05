package com.sbsj.dreamwing.admin.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestListResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityVolunteerRequestListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 봉사활동 신청 대기 목록 화면
 * @author 정은지
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04  	정은지       최초 생성
 * </pre>
 */
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

        fetchRequestList()

        // 툴바
        setSupportActionBar(binding.adminToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.adminToolbar.toolbar.title = "봉사활동 승인 대기 목록"

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
        fetchRequestList()
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
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.volunteer_board -> {
                startActivity(Intent(this, AdminActivity::class.java))
                return true
            }
            R.id.volunteer_request -> {
                startActivity(Intent(this, VolunteerRequestListActivity::class.java))
                return true
            }
            R.id.volunteer_certification -> {
                startActivity(Intent(this, VolunteerCertificationListActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }
}