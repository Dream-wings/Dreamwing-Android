package com.sbsj.dreamwing.volunteer.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.kakao.vectormap.KakaoMap
import com.kakao.vectormap.KakaoMapReadyCallback
import com.kakao.vectormap.MapLifeCycleCallback
import com.sbsj.dreamwing.databinding.FragmentVolunteerDetailBinding
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class VolunteerDetailFragment : Fragment() {

    private var _binding: FragmentVolunteerDetailBinding? = null
    private val binding get() = _binding!!
    private val args: VolunteerDetailFragmentArgs by navArgs()

    // ISO 8601 날짜 포맷
    private val inputDateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
    private val outputDateFormat = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVolunteerDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        loadVolunteerDetails(args.volunteerId)
        initializeMapView()
    }

    private fun loadVolunteerDetails(volunteerId: Long) {
        RetrofitClient.volunteerService.getVolunteerDetail(volunteerId).enqueue(object : Callback<VolunteerDetailResponse> {
            override fun onResponse(call: Call<VolunteerDetailResponse>, response: Response<VolunteerDetailResponse>) {
                if (response.isSuccessful) {
                    val volunteerDetailResponse = response.body()
                    Log.d("VolunteerDetailFragment", "Response body: $volunteerDetailResponse")

                    if (volunteerDetailResponse != null && volunteerDetailResponse.success) {
                        val volunteer = volunteerDetailResponse.data
                        if (volunteer != null) {
                            Log.d("VolunteerDetailFragment", "Volunteer details: $volunteer")

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
                            binding.addressTextView.text = volunteer.address
                            binding.totalCountTextView.text = "총 인원: ${volunteer.totalCount}"
                            binding.statusTextView.text = if (volunteer.status == 1) "모집중" else "모집완료"
                            binding.currentApplicantCountTextView.text = "신청자 수: ${volunteer.currentApplicantCount}"

                            // 날짜 포맷팅
                            binding.volunteerStartDateTextView.text = formatDate(volunteer.volunteerStartDate)
                            binding.volunteerEndDateTextView.text = formatDate(volunteer.volunteerEndDate)
                            binding.recruitStartDateTextView.text = formatDate(volunteer.recruitStartDate)
                            binding.recruitEndDateTextView.text = formatDate(volunteer.recruitEndDate)

                            // 위도와 경도
                            binding.latitudeTextView.text = "위도: ${volunteer.latitude ?: "정보 없음"}"
                            binding.longitudeTextView.text = "경도: ${volunteer.longitude ?: "정보 없음"}"

                            // 이미지 로드
                            val imageUrl = volunteer.imageUrl
                            if (!imageUrl.isNullOrEmpty()) {
                                Picasso.get().load(imageUrl).into(binding.imageView)
                                binding.imageView.visibility = View.VISIBLE
                            } else {
                                binding.imageView.visibility = View.GONE
                            }
                        }
                    } else {
                        Log.e("VolunteerDetailFragment", "API response error: ${volunteerDetailResponse?.message}")
                    }
                } else {
                    Log.e("VolunteerDetailFragment", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<VolunteerDetailResponse>, t: Throwable) {
                Log.e("VolunteerDetailFragment", "Network request failed", t)
            }
        })
    }







    private fun initializeMapView() {
        binding.mapView.start(object : MapLifeCycleCallback() {
            override fun onMapDestroy() {
                Log.e("VolunteerDetailFragment", "onMapDestroy")
            }

            override fun onMapError(error: Exception?) {
                Log.e("VolunteerDetailFragment", "onMapError", error)
            }
        }, object : KakaoMapReadyCallback() {
            override fun onMapReady(kakaoMap: KakaoMap) {
                Log.e("VolunteerDetailFragment", "onMapReady")
                // 위치 정보 설정 없이 지도만 띄우기
            }
        })
    }

    private fun formatDate(dateString: String?): String {
        return try {
            Log.d("VolunteerDetailFragment", "Original date string: $dateString")
            val date = dateString?.let { inputDateFormat.parse(it) }
            date?.let { outputDateFormat.format(it) } ?: "N/A"
        } catch (e: Exception) {
            Log.e("VolunteerDetailFragment", "Date parsing error", e)
            "N/A"
        }
    }
    override fun onResume() {
        super.onResume()
        binding.mapView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mapView.pause()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
