package com.sbsj.dreamwing.support

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivitySupportDetailBinding
import com.sbsj.dreamwing.mission.model.ActivityType
import com.sbsj.dreamwing.support.model.SupportDetailDTO
import com.sbsj.dreamwing.support.model.response.SupportDetailResponse
import com.sbsj.dreamwing.user.LoginActivity
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
/**
 * 후원 상세 정보를 표시하는 액티비티
 * 사용자는 이 액티비티에서 후원에 대한 상세 정보를 확인하고, 후원에 참여할 수 있다.
 * @author 임재성
 * @since 2024.08.04
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   임재성        최초 생성
 * 2024.08.04   임재성        로그인 확인 여부 기능 추가
 * 2024.08.04   임재성        후원 하기 기능 추가
 */
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
            val hasLogin = checkUserLoggedIn()
            if(hasLogin) {
                showDonationDialog()
            }
        }

        if (supportId != -1L) {
            loadSupportDetails(supportId)
        } else {
            Log.e("SupportDetailActivity", "Invalid supportId")
            finish()
        }
    }

    /**
     * 로그인 여부 확인 메서드
     */
    private fun checkUserLoggedIn(): Boolean {
        // 전역 저장소에서 jwt 토큰을 가져옴
        val jwtToken = SharedPreferencesUtil.getToken(this)
        if (jwtToken.isNullOrEmpty()) {
            // 로그인되어 있지 않으면 로그인 요청 다이얼로그를 표시
            showLoginRequestDialog()
            return false
        }
        return true
    }
    /**
     * 로그인 요청 다이얼로그를 표시하는 메서드
     */
    private fun showLoginRequestDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alert, null)
        val confirmTextView = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)

        confirmTextView.text = "로그인이 필요합니다."

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            dialog.dismiss()
            // 로그인 액티비티로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

        dialog.show()
    }
    //후원 상세 정보를 서버에서 로드하여 화면에 표시하는 메서드
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
    //후원 목표 대비 현재 달성률을 계산하는 메서드
    private fun calculateProgressPercentage(goalPoint: Int, currentPoint: Int): Int {
        return if (goalPoint == 0) {
            0
        } else {
            (currentPoint * 100) / goalPoint
        }
    }
    //날짜 문자열을 원하는 형식으로 변환하는 메서드
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
    //기부 다이얼로그를 표시하는 메서드
    private fun showDonationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_text, null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)
        val noButton = dialogView.findViewById<Button>(R.id.noButton)
        val editText = dialogView.findViewById<EditText>(R.id.edit_text)

        message.text = "기부할 금액을 입력하세요."
        editText.inputType = android.text.InputType.TYPE_CLASS_NUMBER

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            val amountStr = editText.text.toString()
            Log.d("SupportDetailActivity", "Entered amount: $amountStr") // 로그 추가

            val amount = amountStr.toIntOrNull()
            if (amount != null && amount > 0) {
                donateToSupport(amount)
                dialog.dismiss()
            } else {
                Toast.makeText(this, "유효한 금액을 입력하세요.", Toast.LENGTH_SHORT).show()
            }
        }

        noButton.setOnClickListener {
            dialog.cancel()
        }

        dialog.show()
    }
    //서버에 기부 요청을 보내는 메서드
    private fun donateToSupport(amount: Int) {
        val supportId = supportDetailDTO.supportId
        val activityTitle = supportDetailDTO.title
        val activityType = ActivityType.SUPPORT.type
        //val userId = 2L // Replace with actual user ID retrieval logic
        // 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        val authHeader = "$jwtToken" // 헤더에 넣을 변수

        if (activityTitle != null) {
            RetrofitClient.supportService.donateForSupport(authHeader,supportId,amount,activityTitle,activityType)
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
    }
    //오류 메시지를 표시하는 다이얼로그를 보여주는 메서드
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
