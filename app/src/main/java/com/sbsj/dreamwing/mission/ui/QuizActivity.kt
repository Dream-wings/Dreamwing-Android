package com.sbsj.dreamwing.mission.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.common.model.ErrorResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityQuizBinding
import com.sbsj.dreamwing.mission.model.ActivityType
import com.sbsj.dreamwing.mission.model.request.AwardPointRequest
import com.sbsj.dreamwing.mission.model.response.QuizResponse
import com.sbsj.dreamwing.user.LoginActivity
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

/**
 * 데일리 퀴즈 화면
 * @author 정은지
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은지        최초 생성
 * 2024.08.03   정은지        답 미선택, 데일리 미션 완료시 토스트 메세지 추가
 */
class QuizActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizBinding
    private var correctAnswer: Int? = null
    private var selectedAnswer: Int? = null
    private var responseMessage: String? = null
    private var isLoading = false
    private lateinit var authHeader : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "데일리 퀴즈"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        getDailyQuiz()

        binding.submitButton.setOnClickListener {
            submitAnswer()
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
        AlertDialog.Builder(this)
            .setTitle("로그인 요청")
            .setMessage("로그인이 필요합니다.")
            .setPositiveButton("확인") { dialog, _ ->
                // 로그인 액티비티로 이동
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
            .show()
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

    private fun getDailyQuiz() {
        RetrofitClient.missionService.getDailyQuiz().enqueue(object :
            Callback<ApiResponse<QuizResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<QuizResponse>>,
                response: Response<ApiResponse<QuizResponse>>
            ) {
                if (response.isSuccessful) {
                    val quizData = response.body()?.data
                    Log.d("QuizActivity", "Quiz: $quizData")
                    quizData?.let {
                        correctAnswer = it.answer
                        binding.questionText.text = it.question
                        binding.option1.text = "1. " + it.choice1
                        binding.option2.text = "2. " + it.choice2
                        binding.option3.text = "3. " + it.choice3
                        binding.option4.text = "4. " + it.choice4

                        setupOptionClickListeners()
                    }
                } else {
                    Log.e("QuizActivity", "Response not successful")
                }
            }
            override fun onFailure(call: Call<ApiResponse<QuizResponse>>, t: Throwable) {  // Corrected the generic type of Call
                Log.e("QuizActivity", "Error: ${t.message}")
            }
        })
    }

    private fun setupOptionClickListeners() {
        val options = listOf(binding.option1, binding.option2, binding.option3, binding.option4)

        options.forEach { option ->
            option.setOnClickListener { view ->
                val selected = (view as TextView)
                options.forEach {
                    it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_off, 0, 0, 0)
                }
                selected.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_check_on, 0, 0, 0)
                selectedAnswer = extractAnswerFromText(selected.text.toString())
            }
        }
    }

    private fun submitAnswer() {
        if (checkUserLoggedIn()) {
            // 토큰 가져오기
            val jwtToken = SharedPreferencesUtil.getToken(this)
            authHeader = "$jwtToken" // 헤더에 넣을 변수
            isLoading = true

            if (selectedAnswer == correctAnswer) {
                // 정답일 경우 QuizCorrectActivity
                awardPoints()
            } else if (selectedAnswer == null ) {
                // 모달 창 띄우기
                Toast.makeText(this, "정답을 선택해 주세요", Toast.LENGTH_SHORT).show()
            } else {
                // 오답일 경우 QuizIncorrectActivity
                startActivity(Intent(this, QuizIncorrectActivity::class.java))
            }
        }
    }

    private fun awardPoints() {
        val request = AwardPointRequest(
            activityType = ActivityType.QUIZ.type,
            activityTitle = ActivityType.QUIZ.title,
            point = ActivityType.QUIZ.point
        )
        RetrofitClient.missionService.awardPoints(authHeader =  authHeader, request).enqueue(object : Callback<ApiResponse<Any>> {
            override fun onResponse(call: Call<ApiResponse<Any>>, response: Response<ApiResponse<Any>>) {
                isLoading = false
                if (response.isSuccessful) {
                    startActivity(Intent(this@QuizActivity, QuizCorrectActivity::class.java))
                } else {
                    val errorResponse = convertErrorBody(response)
                    val errorMessage = errorResponse?.message ?: "Unknown error"

                    if (errorMessage == "이미 포인트를 받았습니다.") {
                        Log.d("QuizActivity", "$errorResponse")
                        Toast.makeText(this@QuizActivity, "이미 데일리 퀴즈를 풀었어요!", Toast.LENGTH_SHORT).show()
                    } else {
                        Log.e("QuizActivity", "서버 오류: $errorResponse")
                    }
                }
            }

            override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                Log.e("QuizActivity", "Request failed: ${t.message}")
            }
        })
    }

    private fun extractAnswerFromText(text: String): Int? {
        return text.substringBefore('.').toIntOrNull()
    }

    // 에러메세지
    private fun convertErrorBody(response: Response<*>): ErrorResponse? {
        return try {
            response.errorBody()?.let {
                val converter: Converter<ResponseBody, ErrorResponse> =
                    RetrofitClient.retrofit.responseBodyConverter(
                        ErrorResponse::class.java,
                        arrayOfNulls<Annotation>(0)
                    )
                converter.convert(it)
            }
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }
}