package com.sbsj.dreamwing.volunteer.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.databinding.ActivityQuizIncorrectBinding
import com.sbsj.dreamwing.databinding.ActivityVolunteerCertificationSuccessBinding

/**
 * 봉사활동 인증 업로드 성공 화면
 * @author 정은지
 * @since 2024.08.03
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은지        최초 생성
 */
class VolunteerCertificationSuccessActivity : AppCompatActivity() {

    private lateinit var binding : ActivityVolunteerCertificationSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerCertificationSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "봉사활동 인증"
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