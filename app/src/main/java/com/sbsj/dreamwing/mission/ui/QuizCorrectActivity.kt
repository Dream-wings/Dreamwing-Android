package com.sbsj.dreamwing.mission.ui

import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityQuizBinding
import com.sbsj.dreamwing.databinding.ActivityQuizCorrectBinding
import com.sbsj.dreamwing.mission.model.ActivityType
import com.sbsj.dreamwing.mission.model.request.AwardPointRequest
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class QuizCorrectActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizCorrectBinding
    private var responseMessage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizCorrectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

//        awardPoints()
    }

    // 툴바 뒤로가기 버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

//    private fun awardPoints() {
//        val request = AwardPointRequest(
//            userId = 1,
//            activityType = ActivityType.QUIZ.type,
//            activityTitle = ActivityType.QUIZ.displayName,
//            point = ActivityType.QUIZ.point
//        )
//
//        RetrofitClient.missionService.awardPoints(request).enqueue(object : Callback<ApiResponse<Any>> {
//            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
//                if (response.isSuccessful) {
//                    val responseMessage = response.body()?.message
//                    Log.d("QuizActivity", "Response: ${response.body()}")
//                    Log.d("QuizActivity", "responseMessage: ${responseMessage}")
//
//                    if (responseMessage == "이미 포인트를 받았습니다.") {
//                        Toast.makeText(this@QuizCorrectActivity, "이미 데일리 퀴즈를 풀었어요!", Toast.LENGTH_SHORT).show()
//                    }
//                } else {
//                    val errorBody = response.errorBody()?.string()
//                    Log.e("QuizActivity", "Response not successful: $errorBody")
//                }
//            }
//
//            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
//                Log.e("QuizActivity", "Request failed: ${t.message}")
//            }
//        })
//    }

}