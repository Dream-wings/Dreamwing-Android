package com.sbsj.dreamwing.mission.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.databinding.ActivityQuizIncorrectBinding

/**
 * 데일리 퀴즈 오답 시 화면
 * @author 정은지
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은지        최초 생성
 */
class QuizIncorrectActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizIncorrectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizIncorrectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    /**
     * 툴바 뒤로가기
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}