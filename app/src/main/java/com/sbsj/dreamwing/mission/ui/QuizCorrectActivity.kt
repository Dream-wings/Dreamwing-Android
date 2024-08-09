package com.sbsj.dreamwing.mission.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.databinding.ActivityQuizCorrectBinding

/**
 * 데일리 퀴즈 정답 시 화면
 * @author 정은지
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은지        최초 생성
 */
class QuizCorrectActivity : AppCompatActivity() {

    private lateinit var binding : ActivityQuizCorrectBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizCorrectBinding.inflate(layoutInflater)
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