package com.sbsj.dreamwing.admin.ui

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.admin.model.request.ApproveRequest
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestDetailResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityVolunteerRequestDetailBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 봉사활동 신청 대기 목록 View Adapter
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
class VolunteerRequestDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerRequestDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerRequestDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바
        setSupportActionBar(binding.adminToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.adminToolbar.toolbar.title = "봉사활동 승인 대기 목록"

        val volunteerId = intent.getLongExtra("volunteerId", 0)
        val userId = intent.getLongExtra("userId", 1L)

        fetchDetails(volunteerId, userId)

        binding.button.setOnClickListener {
            showDialog(volunteerId, userId)
        }
    }

    private fun fetchDetails(volunteerId: Long, userId: Long) {
        RetrofitClient.adminService.getVolunteerRequestDetail(volunteerId, userId)
            .enqueue(object : Callback<ApiResponse<VolunteerRequestDetailResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<VolunteerRequestDetailResponse>>,
                    response: Response<ApiResponse<VolunteerRequestDetailResponse>>
                ) {
                    if (response.isSuccessful) {
                        val detailResponse = response.body()?.data
                        detailResponse?.let {
                            updateUI(it)
                        }
                    } else {
                        Log.e("DetailActivity", "Response not successful")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<VolunteerRequestDetailResponse>>, t: Throwable) {
                    Log.e("DetailActivity", "Error: ${t.message}")
                }
            })
    }

    private fun approveRequest(volunteerId: Long, userId: Long) {
        val request = ApproveRequest(volunteerId, userId)
        RetrofitClient.adminService.approveVolunteerRequest(request)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@VolunteerRequestDetailActivity, "승인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@VolunteerRequestDetailActivity, VolunteerRequestListActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@VolunteerRequestDetailActivity, "실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(this@VolunteerRequestDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI(detail: VolunteerRequestDetailResponse) {
        binding.volunteerTitle.text = detail.title
        binding.loginId.text = detail.loginId
        binding.name.text = detail.name
        binding.phone.text = detail.phone
    }

    private fun showDialog(volunteerId: Long, userId: Long) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("요청 승인")
        builder.setMessage("승인하시겠습니까?")
        builder.setPositiveButton("예") { dialog, _ ->
            approveRequest(volunteerId, userId)
            dialog.dismiss()
        }
        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }

}
