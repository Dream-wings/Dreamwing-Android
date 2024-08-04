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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizCorrectBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

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

}