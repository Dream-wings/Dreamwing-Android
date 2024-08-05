//package com.sbsj.dreamwing.admin
//
//import android.os.Bundle
//import android.widget.ArrayAdapter
//import android.widget.EditText
//import android.widget.Spinner
//import android.widget.Toast
//import androidx.appcompat.app.AppCompatActivity
//import com.sbsj.dreamwing.R
//import com.sbsj.dreamwing.data.api.RetrofitClient
//import com.sbsj.dreamwing.databinding.ActivityCreateItemBinding
//import com.sbsj.dreamwing.support.model.response.SupportDetailResponse
//import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
//import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
//import retrofit2.Call
//import retrofit2.Callback
//import retrofit2.Response
//
//class CreateItemActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityCreateItemBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        binding = ActivityCreateItemBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Spinner 데이터 설정
//        setupSpinners()
//
//        binding.createButton.setOnClickListener {
//            val title = binding.titleEditText.text.toString()
//            val content = binding.contentEditText.text.toString()
//            val type = binding.typeSpinner.selectedItemPosition + 1 // 스피너 인덱스에 따라 타입 설정
//            val category = binding.categorySpinner.selectedItemPosition + 1 // 카테고리 설정
//            val volunteerStartDate = binding.volunteerStartDateEditText.text.toString()
//            val volunteerEndDate = binding.volunteerEndDateEditText.text.toString()
//            val recruitStartDate = binding.recruitStartDateEditText.text.toString()
//            val recruitEndDate = binding.recruitEndDateEditText.text.toString()
//            val address = binding.addressEditText.text.toString()
//            val totalCount = binding.totalCountEditText.text.toString().toIntOrNull() ?: 0
//            val imageUrl = binding.imageUrlEditText.text.toString()
//            val latitude = binding.latitudeEditText.text.toString().toDoubleOrNull() ?: 0.0
//            val longitude = binding.longitudeEditText.text.toString().toDoubleOrNull() ?: 0.0
//
//            // 유효성 검사
//            if (title.isEmpty() || content.isEmpty() || address.isEmpty() ||
//                volunteerStartDate.isEmpty() || volunteerEndDate.isEmpty() ||
//                recruitStartDate.isEmpty() || recruitEndDate.isEmpty()) {
//                Toast.makeText(this, "모든 필드를 채워주세요", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            // VolunteerDetailDTO 객체 생성
//            val volunteerDetail = VolunteerDetailDTO(
//                volunteerId = 0L, // 새로 생성될 항목이므로 ID는 0 또는 null로 설정
//                title = title,
//                content = content,
//                type = type,
//                category = category,
//                volunteerStartDate = volunteerStartDate,
//                volunteerEndDate = volunteerEndDate,
//                address = address,
//                totalCount = totalCount,
//                status = 0, // 기본 상태값 설정 (예: 모집 중)
//                recruitStartDate = recruitStartDate,
//                recruitEndDate = recruitEndDate,
//                imageUrl = imageUrl,
//                latitude = latitude,
//                longitude = longitude
//            )
//
//            // 서버에 데이터 전송
//            createVolunteerItem(volunteerDetail)
//        }
//    }
//
//    private fun setupSpinners() {
//        // 종류 스피너 설정
//        val typeAdapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.volunteer_types, // 봉사 종류 배열 (예: 봉사, 멘토링)
//            android.R.layout.simple_spinner_item
//        )
//        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.typeSpinner.adapter = typeAdapter
//
//        // 카테고리 스피너 설정
//        val categoryAdapter = ArrayAdapter.createFromResource(
//            this,
//            R.array.volunteer_categories, // 봉사 카테고리 배열 (예: 교육, 물품, 문화예술, 주거개선)
//            android.R.layout.simple_spinner_item
//        )
//        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
//        binding.categorySpinner.adapter = categoryAdapter
//    }
//
//    private fun createVolunteerItem(volunteerDetail: VolunteerDetailDTO) {
//        RetrofitClient.volunteerService.createVolunteer(volunteerDetail)
//            .enqueue(object : Callback<VolunteerDetailResponse> {
//                override fun onResponse(
//                    call: Call<VolunteerDetailResponse>,
//                    response: Response<VolunteerDetailResponse>
//                ) {
//                    if (response.isSuccessful) {
//                        val responseBody = response.body()
//                        if (responseBody != null && responseBody.success) {
//                            Toast.makeText(this@CreateItemActivity, "봉사/후원 항목이 생성되었습니다.", Toast.LENGTH_SHORT).show()
//                            finish() // 성공 시 액티비티 종료
//                        } else {
//                            Toast.makeText(this@CreateItemActivity, "생성 실패: ${responseBody?.message}", Toast.LENGTH_SHORT).show()
//                        }
//                    } else {
//                        Toast.makeText(this@CreateItemActivity, "생성 실패: ${response.errorBody()?.string()}", Toast.LENGTH_SHORT).show()
//                    }
//                }
//
//                override fun onFailure(call: Call<VolunteerDetailResponse>, t: Throwable) {
//                    Toast.makeText(this@CreateItemActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
//                }
//            })
//    }
//}
