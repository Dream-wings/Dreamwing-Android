package com.sbsj.dreamwing.user

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityMySupportDetailBinding
import com.sbsj.dreamwing.databinding.ActivityMyVolunteerDetailBinding
import com.sbsj.dreamwing.databinding.ActivityVolunteerDetailBinding
import com.sbsj.dreamwing.user.model.vo.MySupportVO
import com.sbsj.dreamwing.user.model.vo.MyVolunteerVO
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyVolunteerDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyVolunteerDetailBinding
    private lateinit var myVolunteerAdapter: MyVolunteerAdapter
    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up View Binding
        binding = ActivityMyVolunteerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "봉사활동 내역"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Toolbar의 NavigationIcon 클릭 리스너 추가
        binding.toolbar.root.setNavigationOnClickListener {
            onBackPressed() // 이전 페이지로 돌아갑니다.
        }

        // RecyclerView 및 Adapter 설정
        myVolunteerAdapter = MyVolunteerAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyVolunteerDetailActivity)
            adapter = myVolunteerAdapter
        }

        // 리사이클러뷰 스크롤 리스너 추가
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val visibleItemCount = layoutManager.childCount
                val totalItemCount = layoutManager.itemCount
                val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()

                // 마지막 항목에 도달했을 때 다음 페이지 요청
                if (!isLoading && firstVisibleItemPosition + visibleItemCount >= totalItemCount) {
                    currentPage++
                    loadVolunteers()
                }
            }
        })

        // 초기 데이터 로드
        loadVolunteers()
    }

    private fun loadVolunteers() {
        // SharedPreferencesUtil을 사용하여 JWT 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        if (jwtToken.isNullOrEmpty()) {
            // JWT 토큰이 없으면 데이터를 로드하지 않음
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val authHeader = "$jwtToken"

        isLoading = true

        // Retrofit API 호출
        RetrofitClient.userService.getUserVolunteerList(authHeader, currentPage, pageSize)
            .enqueue(object : Callback<ApiResponse<List<MyVolunteerVO>>> {
                override fun onResponse(call: Call<ApiResponse<List<MyVolunteerVO>>>, response: Response<ApiResponse<List<MyVolunteerVO>>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val volunteers = response.body()?.data ?: emptyList()
                        myVolunteerAdapter.addVolunteers(volunteers)
                    } else {
                        // 서버 오류 또는 응답 코드가 2xx가 아닐 경우 처리
                        Toast.makeText(this@MyVolunteerDetailActivity, "서버 오류. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<MyVolunteerVO>>>, t: Throwable) {
                    isLoading = false
                    // 네트워크 오류 처리
                    Toast.makeText(this@MyVolunteerDetailActivity, "네트워크 오류. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}