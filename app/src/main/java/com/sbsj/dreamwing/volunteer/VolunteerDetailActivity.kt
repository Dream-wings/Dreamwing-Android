package com.sbsj.dreamwing.volunteer

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.kakao.vectormap.*
import com.kakao.vectormap.camera.CameraAnimation
import com.kakao.vectormap.camera.CameraUpdateFactory
import com.kakao.vectormap.label.LabelOptions
import com.kakao.vectormap.label.LabelStyle
import com.kakao.vectormap.label.LabelStyles
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityVolunteerDetailBinding
import com.sbsj.dreamwing.user.LoginActivity
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import com.sbsj.dreamwing.volunteer.model.PostApplyVolunteerRequestDTO
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
import com.sbsj.dreamwing.volunteer.ui.VolunteerCertificationActivity
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class VolunteerDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerDetailBinding
    private var kakaoMap: KakaoMap? = null
    private lateinit var volunteerDetailDTO: VolunteerDetailDTO

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

//    private var userId: Long = 2L // Replace with method to get the actual user ID
    private var isApplied = false
    private var isVerified = false // Variable for verification status

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val volunteerId = intent.getLongExtra("volunteerId", -1)
        val volunteerTitle = intent.getStringExtra("volunteerTitle") // Get volunteer title from intent

        //userId = intent.getLongExtra("userId", 2L) // Fetch userId from Intent


        binding.backButton.setOnClickListener {
            finish()

        }

        binding.applyButton.setOnClickListener {
            val hasLogin = checkUserLoggedIn()
            if(hasLogin){
            showConfirmationDialog()}
            else{
                    showLoginRequestDialog()
                }
        }

        binding.cancelApplyButton.setOnClickListener {
            showCancelApplicationDialog()
        }
        binding.certificateButton.setOnClickListener{
            showCertificateDialog(volunteerTitle)
        }

        if (volunteerId != -1L) {
            // 토큰 가져오기
            val jwtToken = SharedPreferencesUtil.getToken(this)
            val authHeader = "$jwtToken" // 헤더에 넣을 변수
            loadVolunteerDetails(volunteerId)
            initializeMapView()
            checkApplicationStatus(volunteerId) // Check if the user has applied
            checkStatus(volunteerId) // Check if the application is approved
        } else {
            Log.e("VolunteerDetailActivity", "Invalid volunteerId")
            finish()
        }

    }

    private fun showCertificateDialog(volunteerTitle: String?) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val confirmTextView = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)
        val noButton = dialogView.findViewById<Button>(R.id.noButton)

        confirmTextView.text = "인증하러 가시겠습니까?"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            // 인증액티비티로 이동
            val intent = Intent(this, VolunteerCertificationActivity::class.java).apply {
                putExtra("volunteerTitle", volunteerTitle) // Pass the title to certification activity
            }
            startActivity(intent)
            finish()
        }

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

    private fun showLoginRequestDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alert, null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)

        message.text = "로그인이 필요합니다."

        val dialog = AlertDialog.Builder(this)
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

    private fun loadVolunteerDetails(volunteerId: Long) {
        val jwtToken = SharedPreferencesUtil.getToken(this)
        val authHeader = "$jwtToken" // 헤더에 넣을 변수
        RetrofitClient.volunteerService.getVolunteerDetail(authHeader,volunteerId)
            .enqueue(object : Callback<VolunteerDetailResponse> {
                override fun onResponse(
                    call: Call<VolunteerDetailResponse>,
                    response: Response<VolunteerDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val volunteerDetailResponse = response.body()
                        Log.d("VolunteerDetailActivity", "Response body: $volunteerDetailResponse")

                        if (volunteerDetailResponse != null && volunteerDetailResponse.success) {
                            volunteerDetailDTO = volunteerDetailResponse.data ?: return

                            binding.titleTextView.text = volunteerDetailDTO.title
                            binding.contentTextView.text = volunteerDetailDTO.content
                            binding.categoryTextView.text = when (volunteerDetailDTO.category) {
                                1 -> "빵만들기"
                                2 -> "자막달기"
                                3 -> "돌보기"
                                4 -> "밑반찬 만들기"
                                5 -> "흙공 만들기"
                                else -> "기타"
                            }
                            binding.addressTextView.text = "봉사 장소   ${(volunteerDetailDTO.address)}"
                            binding.totalCountTextView.text = "/   ${volunteerDetailDTO.totalCount}"
                            binding.currentApplicantCountTextView.text = "모집 인원   ${volunteerDetailDTO.currentApplicantCount}"

                            val startDate = formatDate(volunteerDetailDTO.volunteerStartDate)
                            val endDate = formatDate(volunteerDetailDTO.volunteerEndDate)

                            if (startDate == endDate) {
                                binding.volunteerStartDateTextView.text = "봉사 날짜   $startDate"
                                binding.volunteerEndDateTextView.visibility = View.GONE
                            } else {
                                binding.volunteerStartDateTextView.text = "봉사 시작일   $startDate"
                                binding.volunteerEndDateTextView.text = "~   $endDate"
                                binding.volunteerEndDateTextView.visibility = View.VISIBLE
                            }
                            binding.recruitStartDateTextView.text = "모집 기간   ${formatDate(volunteerDetailDTO.recruitStartDate)}"
                            binding.recruitEndDateTextView.text = "~   ${formatDate(volunteerDetailDTO.recruitEndDate)}"

                            val imageUrl = volunteerDetailDTO.imageUrl
                            if (!imageUrl.isNullOrEmpty()) {
                                Picasso.get().load(imageUrl).into(binding.imageView)
                                binding.imageView.visibility = View.VISIBLE
                            } else {
                                binding.imageView.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.e("VolunteerDetailActivity", "Response error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<VolunteerDetailResponse>, t: Throwable) {
                    Log.e("VolunteerDetailActivity", "Network request failed", t)
                }
            })
    }

    private fun initializeMapView() {
        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.e("VolunteerDetailActivity", "onMapDestroy")
            }

            override fun onMapError(error: Exception?) {
                Log.e("VolunteerDetailActivity", "onMapError", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                Log.e("VolunteerDetailActivity", "onMapReady")
                this@VolunteerDetailActivity.kakaoMap = kakaoMap

                val lat = volunteerDetailDTO.latitude ?: 0.0
                val lon = volunteerDetailDTO.longitude ?: 0.0

                Log.d("VolunteerDetailActivity", "Latitude: $lat, Longitude: $lon")

                val position = LatLng.from(lat, lon)
                val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_marker))
                val options = LabelOptions.from(position).setStyles(styles)
                val labelLayer = kakaoMap.getLabelManager()?.getLayer()

                labelLayer?.let {
                    it.addLabel(options)

                    val cameraUpdate = CameraUpdateFactory.newCenterPosition(position)
                    kakaoMap.moveCamera(cameraUpdate, CameraAnimation.from(500, true, true))
                } ?: run {
                    Log.e("VolunteerDetailActivity", "LabelLayer is null")
                }
            }
        })
    }

    private fun formatDate(dateString: String?): String {
        return try {
            Log.d("VolunteerDetailActivity", "Original date string: $dateString")
            val date = dateString?.let { inputDateFormat.parse(it) }
            date?.let { formatDateWithKoreanWeekday(it) } ?: "N/A"
        } catch (e: Exception) {
            Log.e("VolunteerDetailActivity", "Date parsing error", e)
            "N/A"
        }
    }

    private fun formatDateWithKoreanWeekday(date: Date): String {
        val formattedDate = outputDateFormat.format(date)
        val weekday = SimpleDateFormat("EEE", Locale.ENGLISH).format(date)
        val koreanWeekday = dayOfWeekMap[weekday] ?: "N/A"
        return formattedDate.replace(weekday, koreanWeekday)
    }

    private fun showConfirmationDialog() {

        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)
        val noButton = dialogView.findViewById<Button>(R.id.noButton)

        message.text = "신청하시겠습니까?"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            val requestDTO = PostApplyVolunteerRequestDTO(
                volunteerId = volunteerDetailDTO.volunteerId
            )
            // Call the applyForVolunteer method with the requestDTO
            applyForVolunteer(requestDTO)

            // Dismiss the dialog
            dialog.dismiss()
        }

        noButton.setOnClickListener {
            dialog.dismiss() // Dismiss the dialog on cancel
        }

        dialog.show()

    }

    private fun showCancelApplicationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_confirm, null)
        val message = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)
        val noButton = dialogView.findViewById<Button>(R.id.noButton)

        message.text = "신청을 취소하시겠습니까?"

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            cancelApplication()
            dialog.dismiss()
        }

        noButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun applyForVolunteer(request: PostApplyVolunteerRequestDTO) {
        // 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        val authHeader = "$jwtToken" // 헤더에 넣을 변수

        RetrofitClient.volunteerService.applyForVolunteer(authHeader,request)
            .enqueue(object : Callback<ApiResponse<Unit>> {
                override fun onResponse(
                    call: Call<ApiResponse<Unit>>,
                    response: Response<ApiResponse<Unit>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.success) {
                            Log.d("VolunteerDetailActivity", "Application successful")
                            isApplied = true
                            updateButtonState()
                            showSuccessDialog("신청이 완료되었습니다.")
                        } else {
                            Log.e("VolunteerDetailActivity", "Application failed: ${apiResponse?.message}")
                            showErrorDialog("신청 실패: ${apiResponse?.message}")
                        }
                    } else {
                        Log.e("VolunteerDetailActivity", "Response error: ${response.errorBody()?.string()}")
                        showErrorDialog("신청 실패: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                    Log.e("VolunteerDetailActivity", "Network request failed", t)
                    showErrorDialog("신청 실패: 네트워크 오류")
                }
            })
    }

    private fun cancelApplication() {
        val requestDTO = PostApplyVolunteerRequestDTO(
            volunteerId = volunteerDetailDTO.volunteerId

        )
        // 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        val authHeader = "$jwtToken" // 헤더에 넣을 변수
        RetrofitClient.volunteerService.cancelApplication(authHeader,requestDTO)
            .enqueue(object : Callback<ApiResponse<Unit>> {
                override fun onResponse(
                    call: Call<ApiResponse<Unit>>,
                    response: Response<ApiResponse<Unit>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.success) {
                            Log.d("VolunteerDetailActivity", "Cancellation successful")
                            isApplied = false
                            updateButtonState()
                            showSuccessDialog("신청이 취소되었습니다.")
                        } else {
                            Log.e("VolunteerDetailActivity", "Cancellation failed: ${apiResponse?.message}")
                            showErrorDialog("취소 실패: ${apiResponse?.message}")
                        }
                    } else {
                        Log.e("VolunteerDetailActivity", "Response error: ${response.errorBody()?.string()}")
                        showErrorDialog("취소 실패: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Unit>>, t: Throwable) {
                    Log.e("VolunteerDetailActivity", "Network request failed", t)
                    showErrorDialog("취소 실패: 네트워크 오류")
                }
            })
    }

    // Method to check if the user has applied
    private fun checkApplicationStatus(volunteerId: Long) {

        // 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        val authHeader = "$jwtToken" // 헤더에 넣을 변수

        RetrofitClient.volunteerService.checkApplicationStatus(
            authHeader,volunteerId
        ).enqueue(object : Callback<ApiResponse<Boolean>> {
            override fun onResponse(
                call: Call<ApiResponse<Boolean>>,
                response: Response<ApiResponse<Boolean>>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        isApplied = apiResponse.data ?: false
                        updateButtonState()
                    } else {
                        Log.e("VolunteerDetailActivity", "API response error: ${apiResponse?.message}")
                        updateButtonState()
                    }
                } else {
                    Log.e("VolunteerDetailActivity", "Response error: ${response.errorBody()?.string()}")
                    updateButtonState()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Boolean>>, t: Throwable) {
                Log.e("VolunteerDetailActivity", "Network request failed", t)
                updateButtonState()
            }
        })
    }

    // Method to fetch application approval status from server
    private fun checkStatus(volunteerId: Long) {
        // 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        val authHeader = "$jwtToken" // 헤더에 넣을 변수
        RetrofitClient.volunteerService.checkStatus(authHeader,volunteerId)
            .enqueue(object : Callback<ApiResponse<Int>> {
                override fun onResponse(
                    call: Call<ApiResponse<Int>>,
                    response: Response<ApiResponse<Int>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.success) {
                            val status = apiResponse.data ?: 0
                            updateUIBasedOnStatus(status)
                        } else {
                            Log.e("VolunteerDetailActivity", "API response error: ${apiResponse?.message}")
                        }
                    } else {
                        Log.e("VolunteerDetailActivity", "Response error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Int>>, t: Throwable) {
                    Log.e("VolunteerDetailActivity", "Network request failed", t)
                }
            })
    }

    // Update the UI based on approval status
    private fun updateUIBasedOnStatus(status: Int) {
        if (status == 1) { // If status is approved
            isVerified = true
        }
        updateButtonState()
    }

    private fun updateButtonState() {
        when {
            isVerified -> {
                binding.applyButton.visibility = View.GONE
                binding.cancelApplyButton.visibility = View.GONE
                binding.certificateButton.visibility = View.VISIBLE
                binding.certificateButton.text = "인증하기"
            }
            isApplied -> {
                binding.applyButton.visibility = View.GONE
                binding.cancelApplyButton.visibility = View.VISIBLE
                binding.certificateButton.visibility = View.GONE
            }
            else -> {
                binding.applyButton.visibility = View.VISIBLE
                binding.cancelApplyButton.visibility = View.GONE
                binding.certificateButton.visibility = View.GONE
            }
        }
    }

    private fun showSuccessDialog(message: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alert, null)
        val messageView = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)

        messageView.text = message

        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
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

    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}
