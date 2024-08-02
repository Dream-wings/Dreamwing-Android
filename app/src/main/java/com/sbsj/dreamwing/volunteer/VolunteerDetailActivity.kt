        package com.sbsj.dreamwing.volunteer

        import android.graphics.Color
        import com.kakao.vectormap.*;
        import android.os.Bundle
        import android.util.Log
        import android.view.View
        import android.widget.TextView
        import androidx.appcompat.app.AppCompatActivity
        import com.kakao.vectormap.KakaoMap
        import com.kakao.vectormap.KakaoMapReadyCallback
    //    import com.kakao.vectormap.KakaoMapSdk
        import com.kakao.vectormap.LatLng
        import com.kakao.vectormap.MapLifeCycleCallback
        import com.kakao.vectormap.camera.CameraAnimation
        import com.kakao.vectormap.camera.CameraUpdateFactory
        import com.kakao.vectormap.label.LabelOptions
        import com.kakao.vectormap.label.LabelStyle
        import com.kakao.vectormap.label.LabelStyles
        import com.kakao.vectormap.label.LabelTextStyle
        import com.kakao.vectormap.mapwidget.InfoWindow
        import com.sbsj.dreamwing.R
        import com.sbsj.dreamwing.data.api.RetrofitClient
        import com.sbsj.dreamwing.data.api.RetrofitClient.volunteerService
        import com.sbsj.dreamwing.databinding.ActivityVolunteerDetailBinding
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
            // 지도 위치와 마커 설정
            private var latitude: Double? = null
            private var longitude: Double? = null
            private lateinit var volunteerDetailDTO: VolunteerDetailDTO

            // ISO 8601 날짜 포맷
            private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            private val outputDateFormat = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault())

            // 요일 매핑
            private val dayOfWeekMap = mapOf(
                "Sun" to "일",
                "Mon" to "월",
                "Tue" to "화",
                "Wed" to "수",
                "Thu" to "목",
                "Fri" to "금",
                "Sat" to "토"
            )

            override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)
                binding = ActivityVolunteerDetailBinding.inflate(layoutInflater)
                setContentView(binding.root)
    //            KakaoMapSdk.init(this, "39e30ad984eecf6c8ff66159a2e4ea0b");

                val volunteerId = intent.getLongExtra("volunteerId", -1)

                binding.backButton.setOnClickListener {
                    finish()
                }

                if (volunteerId != -1L) {
                    loadVolunteerDetails(volunteerId)
                    initializeMapView()
                } else {
                    Log.e("VolunteerDetailActivity", "Invalid volunteerId")
                    finish()
                }
            }

            private fun loadVolunteerDetails(volunteerId: Long) {
                RetrofitClient.volunteerService.getVolunteerDetail(volunteerId).enqueue(object : Callback<VolunteerDetailResponse> {
                    override fun onResponse(call: Call<VolunteerDetailResponse>, response: Response<VolunteerDetailResponse>) {
                        if (response.isSuccessful) {
                            val volunteerDetailResponse = response.body()
                            Log.d("VolunteerDetailActivity", "Response body: $volunteerDetailResponse")


                            if (volunteerDetailResponse != null && volunteerDetailResponse.success) {
                                val volunteer = volunteerDetailResponse.data
                                if (volunteer != null) {
                                    Log.d("VolunteerDetailActivity", "Volunteer details: $volunteer")
                                    volunteerDetailDTO = volunteer  // volunteerDetailDTO를 초기화

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
                                    binding.totalCountTextView.text = "/   ${volunteer.totalCount} 명"
        //                            binding.statusTextView.text = if (volunteer.status == 1) "모집중" else "모집완료"
                                    binding.currentApplicantCountTextView.text = "모집 인원   ${volunteer.currentApplicantCount}"

                                    // 날짜 포맷팅
                                    val startDate = formatDate(volunteer.volunteerStartDate)
                                    val endDate = formatDate(volunteer.volunteerEndDate)

                                    // 날짜가 같은지 확인
                                    if (startDate == endDate) {
                                        binding.volunteerStartDateTextView.text = "봉사 날짜   $startDate"
                                        binding.volunteerEndDateTextView.visibility = View.GONE
                                    } else {
                                        binding.volunteerStartDateTextView.text = "봉사 시작일   $startDate"
                                        binding.volunteerEndDateTextView.text = "~   $endDate"
                                        binding.volunteerEndDateTextView.visibility = View.VISIBLE
                                    }
                                    binding.recruitStartDateTextView.text = "모집 기간   ${formatDate(volunteer.recruitStartDate)}"
                                    binding.recruitEndDateTextView.text = "~   ${formatDate(volunteer.recruitEndDate)}"
        //                            // 위도와 경도
        //                            binding.latitudeTextView.text = "위도: ${volunteer.latitude ?: "정보 없음"}"
        //                            binding.longitudeTextView.text = "경도: ${volunteer.longitude ?: "정보 없음"}"

                                    // 이미지 로드
                                    val imageUrl = volunteer.imageUrl
                                    if (!imageUrl.isNullOrEmpty()) {
                                        Picasso.get().load(imageUrl).into(binding.imageView)
                                        binding.imageView.visibility = View.VISIBLE
                                    } else {
                                        binding.imageView.visibility = View.GONE
                                    }

    //                                setMapLocation(latitude, longitude)
                                }
                            } else {
                                Log.e("VolunteerDetailActivity", "API response error: ${volunteerDetailResponse?.message}")
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

    //                    // latitude와 longitude 가져오기
    //                    val latStr = volunteerDetailDTO?.latitude
    //                    val lonStr = volunteerDetailDTO?.longitude

                        val lat = volunteerDetailDTO?.latitude ?: 0.0
                        val lon = volunteerDetailDTO?.longitude ?: 0.0

                        // 로그로 값 확인
                        Log.d("VolunteerDetailActivity", "Latitude: $lat, Longitude: $lon")

                        val position = LatLng.from(lat, lon)
                        // 1. LabelStyles 생성하기 - Icon 이미지 하나만 있는 스타일
                        val styles = LabelStyles.from(LabelStyle.from(R.drawable.ic_marker))

                        // 2. LabelOptions 생성하기
                        val options = LabelOptions.from(position).setStyles(styles)

                        // 3. LabelLayer 가져오기
                        val labelLayer = kakaoMap.getLabelManager()?.getLayer()

                        // 4. LabelLayer에 LabelOptions을 넣어 Label 생성하기
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
