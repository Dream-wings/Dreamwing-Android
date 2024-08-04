package com.sbsj.dreamwing.mission.ui

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.databinding.ActivityWalkAwardBinding

class WalkAwardActivity : AppCompatActivity() {

    private lateinit var binding : ActivityWalkAwardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkAwardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val point = intent.getIntExtra("point", 0)
        val activityTitle = intent.getStringExtra("activityTitle")
        val steps = activityTitle?.split(" ")?.getOrNull(1) ?: ""
        val message = steps + " 걸음을 달성했어요!"

        binding.message.text = message
        binding.point.text = "+" + point.toString()

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