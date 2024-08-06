package com.sbsj.dreamwing.admin.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.admin.model.request.ApproveRequest
import com.sbsj.dreamwing.admin.model.request.AwardVolunteerPointRequest
import com.sbsj.dreamwing.admin.model.response.VolunteerCertificationDetailResponse
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestDetailResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityVolunteerCertificationDetailBinding
import com.sbsj.dreamwing.databinding.ActivityVolunteerRequestDetailBinding
import com.sbsj.dreamwing.mission.model.ActivityType
import com.sbsj.dreamwing.mission.model.request.AwardPointRequest
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VolunteerCertificationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerCertificationDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityVolunteerCertificationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 툴바
        setSupportActionBar(binding.adminToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.adminToolbar.toolbar.title = "봉사활동 인증 대기 목록"

        val volunteerId = intent.getLongExtra("volunteerId", 0)
        val userId = intent.getLongExtra("userId", 1L)
        val activityType = intent.getIntExtra("activityType", 0)
        val activityTitle = intent.getStringExtra("activityTitle")
        val point = intent.getIntExtra("point", 5000)

        fetchDetails(volunteerId, userId)

        binding.button.setOnClickListener {
            val request = AwardVolunteerPointRequest(
                volunteerId = volunteerId,
                userId = userId,
                activityType = activityType,
                activityTitle = activityTitle?: "null",
                point = point
            )
            showDialog(request)
        }
    }

    private fun fetchDetails(volunteerId: Long, userId: Long) {
        RetrofitClient.adminService.getVolunteerCertificationDetail(volunteerId, userId)
            .enqueue(object : Callback<ApiResponse<VolunteerCertificationDetailResponse>> {
                override fun onResponse(
                    call: Call<ApiResponse<VolunteerCertificationDetailResponse>>,
                    response: Response<ApiResponse<VolunteerCertificationDetailResponse>>
                ) {
                    if (response.isSuccessful) {
                        val detailResponse = response.body()?.data
                        detailResponse?.let {
                            updateUI(it)
                        }
                    } else {
                        Log.e("VolunteerCertificationDetailActivity", "Response not successful")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<VolunteerCertificationDetailResponse>>, t: Throwable) {
                    Log.e("DetailActivity", "Error: ${t.message}")
                }
            })
    }

    private fun awardPoint(request : AwardVolunteerPointRequest) {
        RetrofitClient.adminService.awardVolunteerPoints(request)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@VolunteerCertificationDetailActivity, "승인이 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@VolunteerCertificationDetailActivity, VolunteerCertificationListActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@VolunteerCertificationDetailActivity, "실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(this@VolunteerCertificationDetailActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun updateUI(detail: VolunteerCertificationDetailResponse) {
        binding.volunteerTitle.text = detail.title
        binding.loginId.text = detail.loginId
        binding.image

        Picasso.get()
            .load(detail.imageUrl)
            .placeholder(R.drawable.bg_round_box_stroke)
            .error(R.drawable.selector_request_button)
            .into(binding.image)
    }

    private fun showDialog(request : AwardVolunteerPointRequest) {
        val builder = android.app.AlertDialog.Builder(this)
        builder.setTitle("요청 승인")
        builder.setMessage("승인하시겠습니까?")
        builder.setPositiveButton("예") { dialog, _ ->
            awardPoint(request)
            dialog.dismiss()
        }
        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }

        val dialog = builder.create()
        dialog.show()
    }
}