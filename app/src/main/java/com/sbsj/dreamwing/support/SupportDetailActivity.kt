package com.sbsj.dreamwing.support

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivitySupportDetailBinding
import com.sbsj.dreamwing.support.model.SupportDetailDTO
import com.sbsj.dreamwing.support.model.response.SupportDetailResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupportDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportDetailBinding
    private lateinit var supportDetailDTO: SupportDetailDTO

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val supportId = intent.getLongExtra("supportId", -1)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.donateButton.setOnClickListener {
            showDonationDialog()
        }

        if (supportId != -1L) {
            loadSupportDetails(supportId)
        } else {
            Log.e("SupportDetailActivity", "Invalid supportId")
            finish()
        }
    }

    private fun loadSupportDetails(supportId: Long) {
        RetrofitClient.supportService.getSupportDetail(supportId)
            .enqueue(object : Callback<SupportDetailResponse> {
                override fun onResponse(
                    call: Call<SupportDetailResponse>,
                    response: Response<SupportDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val supportDetailResponse = response.body()
                        Log.d("SupportDetailActivity", "Response body: $supportDetailResponse")

                        if (supportDetailResponse != null && supportDetailResponse.success) {
                            supportDetailDTO = supportDetailResponse.data ?: return

                            binding.titleTextView.text = supportDetailDTO.title
                            binding.contentTextView.text = supportDetailDTO.content
                            binding.totalAmountTextView.text = "${supportDetailDTO.goalPoint} WING 목표"
                            binding.currentAmountTextView.text = "${supportDetailDTO.currentPoint} WING"

                            val progressPercentage = calculateProgressPercentage(supportDetailDTO.goalPoint, supportDetailDTO.currentPoint)
                            binding.progressBar.progress = progressPercentage
                            binding.progressPercentageTextView.text = "$progressPercentage%"

                            binding.fundraisingPeriodTextView.text = "모금 기간 : ${formatDate(supportDetailDTO.startDate.toString())} ~ ${formatDate(supportDetailDTO.endDate.toString())}"

                            val imageUrl = supportDetailDTO.imageUrl
                            if (!imageUrl.isNullOrEmpty()) {
                                Picasso.get().load(imageUrl).into(binding.imageView)
                                binding.imageView.visibility = View.VISIBLE
                            } else {
                                binding.imageView.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.e("SupportDetailActivity", "Response error: ${response.errorBody()?.string()}")
                        showErrorDialog("상세 정보를 가져오는 데 실패했습니다.")
                    }
                }

                override fun onFailure(call: Call<SupportDetailResponse>, t: Throwable) {
                    Log.e("SupportDetailActivity", "Network request failed", t)
                    showErrorDialog("상세 정보를 가져오는 데 실패했습니다: 네트워크 오류")
                }
            })
    }

    private fun calculateProgressPercentage(goalPoint: Int, currentPoint: Int): Int {
        return if (goalPoint == 0) {
            0
        } else {
            (currentPoint * 100) / goalPoint
        }
    }

    private fun formatDate(dateString: String?): String {
        return try {
            Log.d("SupportDetailActivity", "Original date string: $dateString")
            val date = dateString?.let { inputDateFormat.parse(it) }
            date?.let { outputDateFormat.format(it) } ?: "N/A"
        } catch (e: Exception) {
            Log.e("SupportDetailActivity", "Date parsing error", e)
            "N/A"
        }
    }

    private fun showDonationDialog() {
        val donationDialog = AlertDialog.Builder(this)
        donationDialog.setTitle("기부하기")
        donationDialog.setMessage("기부할 금액을 입력하세요:")

        val input = android.widget.EditText(this)
        input.inputType = android.text.InputType.TYPE_CLASS_NUMBER
        donationDialog.setView(input)

        donationDialog.setPositiveButton("확인") { dialog, _ ->
            val amount = input.text.toString().toIntOrNull()
            if (amount != null && amount > 0) {
                donateToSupport(amount)
            } else {
                Toast.makeText(this, "유효한 금액을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        donationDialog.setNegativeButton("취소") { dialog, _ ->
            dialog.cancel()
        }

        donationDialog.show()
    }

    private fun donateToSupport(amount: Int) {
        val supportId = supportDetailDTO.supportId
        val userId = 2L // Replace with actual user ID retrieval logic

        RetrofitClient.supportService.donateForSupport(supportId, userId, amount)
            .enqueue(object : Callback<ApiResponse<Unit>> {
                override fun onResponse(call: Call<ApiResponse<Unit>>, response: Response<ApiResponse<Unit>>) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(this@SupportDetailActivity, "기부가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                        loadSupportDetails(supportId) // Reload details to update current points
                    } else {
                        Log.e("SupportDetailActivity", "Donation failed: ${response.errorBody()?.string()}")
                        showErrorDialog("기부 실패: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                    Log.e("SupportDetailActivity", "Network request failed", t)
                    showErrorDialog("기부 실패: 네트워크 오류")
                }
            })
    }

    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("오류")
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }
}
