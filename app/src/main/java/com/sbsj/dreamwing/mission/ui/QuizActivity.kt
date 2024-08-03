package com.sbsj.dreamwing.mission.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityQuizBinding
import com.sbsj.dreamwing.mission.model.response.QuizResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 데일리 퀴즈 화면
 * @author 정은지
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은지        최초 생성
 */
class QuizActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizBinding
    private var correctAnswer: Int? = null
    private var selectedAnswer: Int? = null

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
        if (selectedAnswer == correctAnswer) {
            // 정답일 경우 QuizCorrectActivity
            startActivity(Intent(this, QuizCorrectActivity::class.java))
        } else if (selectedAnswer == null ) {
            // 모달 창 띄우기
        } else {
            // 오답일 경우 QuizIncorrectActivity
            startActivity(Intent(this, QuizIncorrectActivity::class.java))
        }
        finish()
    }

    private fun extractAnswerFromText(text: String): Int? {
        return text.substringBefore('.').toIntOrNull()
    }

}