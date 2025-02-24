package com.sbsj.dreamwing.user.ui

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.user.model.vo.MyPointVO
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import com.sbsj.dreamwing.databinding.ActivityPointDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 회원의 포인트 내역을 보여주는 액티비티
 * @author 정은찬
 * @since 2024.08.04
 *
 * <pre>
 * 수정일             수정자       				    수정내용
 * ----------  ----------------    --------------------------------------------------------------------------
 *  2024.08.04     	정은찬        		        최초 생성
 * </pre>
 */
class MyPointDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPointDetailBinding
    private lateinit var pointAdapter: MyPointAdapter
    private var currentPage = 0
    private val pageSize = 20
    private var isLoading = false

    /**
     * 액티비티 생성 호출
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Set up View Binding
        binding = ActivityPointDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Toolbar 설정
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "포인트 내역"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // Toolbar의 NavigationIcon 클릭 리스너 추가
        binding.toolbar.root.setNavigationOnClickListener {
            onBackPressed() // 이전 페이지로 돌아갑니다.
        }

        // RecyclerView 및 Adapter 설정
        pointAdapter = MyPointAdapter()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MyPointDetailActivity)
            adapter = pointAdapter
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
                    loadPoints()
                }
            }
        })

        // 초기 데이터 로드
        loadPoints()
    }

    /*
     * 포인트 데이터를 서버에서 로드하는 메서드
     */
    private fun loadPoints() {
        // SharedPreferencesUtil을 사용하여 JWT 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        if (jwtToken.isNullOrEmpty()) {
            // JWT 토큰이 없으면 데이터를 로드하지 않음
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val authHeader = "$jwtToken"
        Log.d("PointDetailActivity", "jwtToken: $authHeader")

        isLoading = true

        // Retrofit API 호출
        RetrofitClient.userService.getUserPointList(authHeader, currentPage, pageSize)
            .enqueue(object : Callback<ApiResponse<List<MyPointVO>>> {
                override fun onResponse(call: Call<ApiResponse<List<MyPointVO>>>, response: Response<ApiResponse<List<MyPointVO>>>) {
                    isLoading = false
                    if (response.isSuccessful) {
                        val points = response.body()?.data ?: emptyList()
                        pointAdapter.addPoints(points)
                    } else {
                        // 서버 오류 또는 응답 코드가 2xx가 아닐 경우 처리
                        Toast.makeText(this@MyPointDetailActivity, "서버 오류. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<List<MyPointVO>>>, t: Throwable) {
                    isLoading = false
                    // 네트워크 오류 처리
                    Toast.makeText(this@MyPointDetailActivity, "네트워크 오류. 나중에 다시 시도해주세요.", Toast.LENGTH_SHORT).show()
                }
            })
    }
}
