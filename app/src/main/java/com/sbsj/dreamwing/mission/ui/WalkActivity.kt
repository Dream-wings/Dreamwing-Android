package com.sbsj.dreamwing.mission.ui

import android.Manifest
import android.content.BroadcastReceiver
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.databinding.ActivityWalkBinding

/**
 * 드림워크 화면
 * author 정은지
 * since 2024.08.03
 * version 1.0
 *
 * 수정일            수정자         수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은지        최초 생성
 * 2024.08.04   정은지        포인트 부여 기능 추가
 */
class WalkActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var binding: ActivityWalkBinding
    private var stepCountSensor: Sensor? = null
    private lateinit var tvStepCount: TextView
    private var totalSteps = 0f
    private var currentSteps = 0f
    private var previousTotalSteps = 0f
    private lateinit var walk4000Button: Button
    private lateinit var walk7000Button: Button
    private lateinit var walk10000Button: Button
    private lateinit var requestPointButton: Button

    private val TYPE = Sensor.TYPE_STEP_COUNTER // 보행 계수기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "드림워크"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        tvStepCount = binding.stepCount

        walk4000Button = binding.walk4000
        walk7000Button = binding.walk7000
        walk10000Button = binding.walk10000
        requestPointButton = binding.requestPoint

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(TYPE)

        loadPreviousTotalSteps()
//        setStepCountReset()

        updateButtonState(previousTotalSteps)

        val progressPercentage = calculateProgressPercentage(10000, previousTotalSteps.toInt())
        Log.d(TAG, "progress Percentage: ${progressPercentage}")
        binding.progressBar.progress = progressPercentage

        Log.d(TAG, "onCrate previousTotalSteps: ${previousTotalSteps}")

        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor!!", Toast.LENGTH_SHORT).show()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                Toast.makeText(this, "No Permission!!", Toast.LENGTH_SHORT).show()
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
            }
        }

        walk4000Button.setOnClickListener {
            onWalk4000ButtonClick()
        }

        walk7000Button.setOnClickListener {
            onWalk7000ButtonClick()
        }

        walk10000Button.setOnClickListener {
            onWalk10000ButtonClick()
        }

        requestPointButton.setOnClickListener {
            onRequestPointButtonClick()
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

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) {
        Log.d(TAG, "requestPermissionLauncher: 건수 : ${it.size}")

        var results = true
        it.values.forEach { granted ->
            if (!granted) {
                results = false
                return@forEach
            }
        }

        if (!results) {
            Toast.makeText(this@WalkActivity, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if (p0?.sensor?.type == TYPE) {
            Log.d(TAG, "onSensorChanged: ${p0.values[0]}")
            totalSteps = p0.values[0]
            val currentSteps = totalSteps - previousTotalSteps

            Log.d(TAG, "currentSteps: ${currentSteps}" )

            val decimalFormat = DecimalFormat("#,###")
//            val formattedStepCount: String = decimalFormat.format(currentSteps.toInt())
            val formattedStepCount: String = decimalFormat.format(totalSteps.toInt())

            tvStepCount.text = formattedStepCount
            updateButtonState(totalSteps)
        }
    }

    // 버튼 활성화 처리
    private fun updateButtonState(steps: Float) {
        if (steps >= 10000.0) {
            Log.d("WalkActivity", "walk10000Button 활성화")
            walk10000Button.isEnabled = true
        }

        if (steps >= 7000.0) {
            Log.d("WalkActivity", "walk7000Button 활성화")
            walk7000Button.isEnabled = true
        }

        if (steps >= 4000.0) {
            Log.d("WalkActivity", "walk4000Button 활성화")
            walk4000Button.isEnabled = true
        }
    }

    private fun onWalk4000ButtonClick() {
        Toast.makeText(this, "Walk 4000 button clicked!", Toast.LENGTH_SHORT).show()
        walk4000Button.setBackgroundResource(R.drawable.bg_round_box2)
        walk7000Button.setBackgroundResource(R.drawable.selector_walk_button)
        walk10000Button.setBackgroundResource(R.drawable.selector_walk_button)
        requestPointButton.isEnabled = true

    }

    private fun onWalk7000ButtonClick() {
        Toast.makeText(this, "Walk 7000 button clicked!", Toast.LENGTH_SHORT).show()
        walk7000Button.setBackgroundResource(R.drawable.bg_round_box2)
        walk4000Button.setBackgroundResource(R.drawable.selector_walk_button)
        walk10000Button.setBackgroundResource(R.drawable.selector_walk_button)
        requestPointButton.isEnabled = true
    }

    private fun onWalk10000ButtonClick() {
        Toast.makeText(this, "Walk 10000 button clicked!", Toast.LENGTH_SHORT).show()
        walk10000Button.setBackgroundResource(R.drawable.bg_round_box2)
        walk7000Button.setBackgroundResource(R.drawable.selector_walk_button)
        walk4000Button.setBackgroundResource(R.drawable.selector_walk_button)
        requestPointButton.isEnabled = true
    }

    private fun onRequestPointButtonClick() {
        Toast.makeText(this, "Request Point button clicked!", Toast.LENGTH_SHORT).show()
        // 여기에 버튼 클릭 시 수행할 작업을 추가하세요
    }

    private fun loadPreviousTotalSteps() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        previousTotalSteps = sharedPreferences.getFloat("previousTotalSteps", 0f)
    }

    private fun savePreviousTotalSteps() {
        val sharedPreferences = getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putFloat("previousTotalSteps", totalSteps)
        editor.apply()
    }

//    class ResetStepReceiver : BroadcastReceiver() {
//        override fun onReceive(context: Context, intent: Intent?) {
//            val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
//            val editor = sharedPreferences.edit()
//            val currentSteps = sharedPreferences.getFloat("currentSteps", 0f)
//            editor.putFloat("previousTotalSteps", currentSteps)
//            editor.apply()
//        }
//    }

    override fun onResume() {
        super.onResume()
        sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
        savePreviousTotalSteps() // Save steps when paused
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {}

    private fun calculateProgressPercentage(goalSteps: Int, currentSteps: Int): Int {
        return if (goalSteps == 0 || currentSteps == 0) {
            0
        } else {
            (currentSteps * 100) / goalSteps
        }
    }
}
