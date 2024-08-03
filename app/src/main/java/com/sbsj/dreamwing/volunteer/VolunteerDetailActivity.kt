package com.sbsj.dreamwing.volunteer

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
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
import com.sbsj.dreamwing.volunteer.model.PostApplyVolunteerRequestDTO
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
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
    private var latitude: Double? = null
    private var longitude: Double? = null
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

    private var userId: Long = 2L // Replace with method to get the actual user ID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val volunteerId = intent.getLongExtra("volunteerId", -1)
        userId = intent.getLongExtra("userId", 2L) // Fetch userId from Intent

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.applyButton.setOnClickListener {
            showConfirmationDialog()
        }

        if (volunteerId != -1L) {
            loadVolunteerDetails(volunteerId)
            initializeMapView()
            checkIfUserApplied(volunteerId, userId) // Check if the user has applied
        } else {
            Log.e("VolunteerDetailActivity", "Invalid volunteerId")
            finish()
        }
    }

    private fun loadVolunteerDetails(volunteerId: Long) {
        RetrofitClient.volunteerService.getVolunteerDetail(volunteerId)
            .enqueue(object : Callback<VolunteerDetailResponse> {
                override fun onResponse(
                    call: Call<VolunteerDetailResponse>,
                    response: Response<VolunteerDetailResponse>
                ) {
                    if (response.isSuccessful) {
                        val volunteerDetailResponse = response.body()
                        Log.d("VolunteerDetailActivity", "Response body: $volunteerDetailResponse")

                        if (volunteerDetailResponse != null && volunteerDetailResponse.success) {
                            val volunteer = volunteerDetailResponse.data
                            if (volunteer != null) {
                                Log.d("VolunteerDetailActivity", "Volunteer details: $volunteer")
                                volunteerDetailDTO = volunteer

                                binding.titleTextView.text = volunteer.title
                                binding.contentTextView.text = volunteer.content
                                binding.categoryTextView.text = when (volunteer.category) {
                                    1 -> "빵만들기"
                                    2 -> "자막달기"
                                    3 -> "돌보기"
                                    4 -> "밑반찬 만들기"
                                    5 -> "흙공 만들기"
                                    else -> "기타"
                                }
                                binding.addressTextView.text = "봉사 장소   ${(volunteer.address)}"
                                binding.totalCountTextView.text = "/   ${volunteer.totalCount}"
                                binding.currentApplicantCountTextView.text =
                                    "모집 인원   ${volunteer.currentApplicantCount}"

                                val startDate = formatDate(volunteer.volunteerStartDate)
                                val endDate = formatDate(volunteer.volunteerEndDate)

                                if (startDate == endDate) {
                                    binding.volunteerStartDateTextView.text = "봉사 날짜   $startDate"
                                    binding.volunteerEndDateTextView.visibility = View.GONE
                                } else {
                                    binding.volunteerStartDateTextView.text = "봉사 시작일   $startDate"
                                    binding.volunteerEndDateTextView.text = "~   $endDate"
                                    binding.volunteerEndDateTextView.visibility = View.VISIBLE
                                }
                                binding.recruitStartDateTextView.text =
                                    "모집 기간   ${formatDate(volunteer.recruitStartDate)}"
                                binding.recruitEndDateTextView.text =
                                    "~   ${formatDate(volunteer.recruitEndDate)}"

                                val imageUrl = volunteer.imageUrl
                                if (!imageUrl.isNullOrEmpty()) {
                                    Picasso.get().load(imageUrl).into(binding.imageView)
                                    binding.imageView.visibility = View.VISIBLE
                                } else {
                                    binding.imageView.visibility = View.GONE
                                }
                            }
                        } else {
                            Log.e(
                                "VolunteerDetailActivity",
                                "API response error: ${volunteerDetailResponse?.message}"
                            )
                        }
                    } else {
                        Log.e(
                            "VolunteerDetailActivity",
                            "Response error: ${response.errorBody()?.string()}"
                        )
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
        AlertDialog.Builder(this)
            .setTitle("신청 확인")
            .setMessage("신청을 확정합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                val requestDTO = PostApplyVolunteerRequestDTO(
                    volunteerId = volunteerDetailDTO.volunteerId,
                    userId = userId
                )
                applyForVolunteer(requestDTO)
                dialog.dismiss()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    private fun applyForVolunteer(request: PostApplyVolunteerRequestDTO) {
        RetrofitClient.volunteerService.applyForVolunteer(request)
            .enqueue(object : Callback<ApiResponse<Unit>> {
                override fun onResponse(
                    call: Call<ApiResponse<Unit>>,
                    response: Response<ApiResponse<Unit>>
                ) {
                    if (response.isSuccessful) {
                        val apiResponse = response.body()
                        if (apiResponse != null && apiResponse.success) {
                            Log.d("VolunteerDetailActivity", "Application successful")
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

    private fun checkIfUserApplied(volunteerId: Long, userId: Long) {
        RetrofitClient.volunteerService.checkIfAlreadyApplied(
            PostApplyVolunteerRequestDTO(volunteerId, userId)
        ).enqueue(object : Callback<ApiResponse<Boolean>> {
            override fun onResponse(
                call: Call<ApiResponse<Boolean>>,
                response: Response<ApiResponse<Boolean>>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        val hasApplied = apiResponse.data ?: false
                        // Show or hide the apply button and certificate button based on application status
                        if (hasApplied) {
                            binding.applyButton.visibility = View.GONE
                            binding.certificateButton.visibility = View.VISIBLE
                        } else {
                            binding.applyButton.visibility = View.VISIBLE
                            binding.certificateButton.visibility = View.GONE
                        }
                    } else {
                        Log.e("VolunteerDetailActivity", "API response error: ${apiResponse?.message}")
                        // Handle case where response is not successful
                        binding.applyButton.visibility = View.GONE
                        binding.certificateButton.visibility = View.GONE
                    }
                } else {
                    Log.e("VolunteerDetailActivity", "Response error: ${response.errorBody()?.string()}")
                    // Handle case where response is not successful
                    binding.applyButton.visibility = View.GONE
                    binding.certificateButton.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ApiResponse<Boolean>>, t: Throwable) {
                Log.e("VolunteerDetailActivity", "Network request failed", t)
                // Handle network failure
                binding.applyButton.visibility = View.GONE
                binding.certificateButton.visibility = View.GONE
            }
        })
    }


    private fun showSuccessDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("성공")
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ ->
                dialog.dismiss()
                finish() // Optionally finish the activity after success
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

    private fun getUserId(): Long {
        // Replace with actual method to get user ID from preferences or session
        return 2L // Example user ID
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
//        binding.mapView.destroy()
    }
}
