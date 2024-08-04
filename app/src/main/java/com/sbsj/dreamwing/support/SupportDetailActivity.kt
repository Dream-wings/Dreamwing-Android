package com.sbsj.dreamwing.support

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivitySupportDetailBinding
import com.sbsj.dreamwing.support.model.SupportCategory
import com.sbsj.dreamwing.support.model.SupportDetailDTO
import com.sbsj.dreamwing.support.model.response.SupportDetailResponse
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*

class SupportDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySupportDetailBinding
    private lateinit var supportDetailDTO: SupportDetailDTO

    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault())

    private val dayOfWeekMap = mapOf(
        "Sun" to "일",
        "Mon" to "월",
        "Tue" to "화",
        "Wed" to "수",
        "Thu" to "목",
        "Fri" to "금",
        "Sat" to "토"
    )

    private var userId: Long = 2L // Replace with method to get the actual user ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySupportDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val supportId = intent.getLongExtra("supportId", -1)
        userId = intent.getLongExtra("userId", 2L) // Fetch userId from Intent

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

                            // Use enum for category
                            val category = SupportCategory.fromValue(supportDetailDTO.category)
                            binding.categoryTextView.text = category.displayName

                            binding.totalAmountTextView.text = "목표 금액: ${supportDetailDTO.goalPoint} WING"
                            binding.currentAmountTextView.text = "모금액: ${supportDetailDTO.currentPoint} WING"

                            val recruitStartDate = formatDate(supportDetailDTO.recruitStartDate)
                            val recruitEndDate = formatDate(supportDetailDTO.recruitEndDate)

                            binding.recruitStartDateTextView.text = "모금 시작일: $recruitStartDate"
                            binding.recruitEndDateTextView.text = "모금 종료일: $recruitEndDate"

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
                    }
                }

                override fun onFailure(call: Call<SupportDetailResponse>, t: Throwable) {
                    Log.e("SupportDetailActivity", "Network request failed", t)
                }
            })
    }

    private fun formatDate(dateString: String?): String {
        return try {
            Log.d("SupportDetailActivity", "Original date string: $dateString")
            val date = dateString?.let { inputDateFormat.parse(it) }
            date?.let { formatDateWithKoreanWeekday(it) } ?: "N/A"
        } catch (e: Exception) {
            Log.e("SupportDetailActivity", "Date parsing error", e)
            "N/A"
        }
    }

    private fun formatDateWithKoreanWeekday(date: Date): String {
        val formattedDate = outputDateFormat.format(date)
        val weekday = SimpleDateFormat("EEE", Locale.ENGLISH).format(date)
        val koreanWeekday = dayOfWeekMap[weekday] ?: "N/A"
        return formattedDate.replace(weekday, koreanWeekday)
    }

    private fun showDonationDialog() {
        // Create an EditText to input the donation amount
        val input = EditText(this)
        input.hint = "기부할 금액을 입력하세요"

        AlertDialog.Builder(this)
            .setTitle("기부하기")
            .setMessage("기부할 금액을 입력하세요.")
            .setView(input)
            .setPositiveButton("확인") { dialog, _ ->
                val donationAmount = input.text.toString().toIntOrNull()
                if (donationAmount != null && donationAmount > 0) {
                    donateForSupport(supportDetailDTO.supportId, userId, donationAmount)
                } else {
                    Toast.makeText(this, "유효한 금액을 입력하세요.", Toast.LENGTH_SHORT).show()
                }
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun donateForSupport(supportId: Long, userId: Long, amount: Int) {
        RetrofitClient.supportService.donateForSupport(supportId, userId, amount)
            .enqueue(object : Callback<ApiResponse<Unit>> {
                override fun onResponse(
                    call: Call<ApiResponse<Unit>>,
                    response: Response<ApiResponse<Unit>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.success) {
                            Log.d("SupportDetailActivity", "Donation successful")
                            showSuccessDialog("기부가 완료되었습니다.")
                            loadSupportDetails(supportId) // Reload details to update current amount
                        } else {
                            Log.e("SupportDetailActivity", "Donation failed: ${apiResponse?.message}")
                            showErrorDialog("기부 실패: ${apiResponse?.message}")
                        }
                    } else {
                        Log.e("SupportDetailActivity", "Response error: ${response.errorBody()?.string()}")
                        showErrorDialog("기부 실패: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                    Log.e("SupportDetailActivity", "Network request failed", t)
                    showErrorDialog("기부 실패: 네트워크 오류")
                }
            })
    }

    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("성공")
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
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
