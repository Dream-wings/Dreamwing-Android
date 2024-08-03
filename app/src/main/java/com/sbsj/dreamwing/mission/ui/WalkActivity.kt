package com.sbsj.dreamwing.mission.ui

import android.Manifest
import android.app.AlarmManager
import android.app.PendingIntent
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
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sbsj.dreamwing.databinding.ActivityWalkBinding
import java.util.Calendar

/**
 * 드림워크 화면
 * author 정은지
 * since 2024.08.03
 * version 1.0
 *
 * 수정일            수정자         수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은지        최초 생성
 */
class WalkActivity : AppCompatActivity(), SensorEventListener {

    private lateinit var sensorManager: SensorManager
    private lateinit var binding: ActivityWalkBinding
    private var stepCountSensor: Sensor? = null
    private lateinit var tvStepCount: TextView
    private var totalSteps = 0f
    private var previousTotalSteps = 0f

    private val TYPE = Sensor.TYPE_STEP_COUNTER // 보행 계수기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWalkBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        tvStepCount = binding.stepCount
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(TYPE)

        loadPreviousTotalSteps()
        setStepCountReset()

        if (stepCountSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor!!", Toast.LENGTH_SHORT).show()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                Toast.makeText(this, "No Permission!!", Toast.LENGTH_SHORT).show()
                // ask for permission
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))
            }
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

            val decimalFormat = DecimalFormat("#,###")
            val formattedStepCount: String = decimalFormat.format(currentSteps.toInt())

            tvStepCount.text = formattedStepCount
        }
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

    // 하루마다 걸음 수 초기화
    private fun setStepCountReset() {
        val calendar = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
        }

        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(this, ResetStepReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)

        alarmManager.setInexactRepeating(
            AlarmManager.RTC_WAKEUP,
            calendar.timeInMillis,
            AlarmManager.INTERVAL_DAY,
            pendingIntent
        )
    }

    // 테스트 1분마다 초기화
//    private fun setStepCountReset() {
//        val calendar = Calendar.getInstance().apply {
//            timeInMillis = System.currentTimeMillis()
//            add(Calendar.MINUTE, 1) // 현재 시간으로부터 1분 후로 설정
//        }
//
//        val alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager
//        val intent = Intent(this, ResetStepReceiver::class.java)
//        val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
//
//        alarmManager.setExact(
//            AlarmManager.RTC_WAKEUP,
//            calendar.timeInMillis,
//            pendingIntent
//        )
//    }


    class ResetStepReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            val sharedPreferences = context.getSharedPreferences("myPrefs", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            val currentSteps = sharedPreferences.getFloat("currentSteps", 0f)
            editor.putFloat("previousTotalSteps", currentSteps)
            editor.apply()
        }
    }

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
}
