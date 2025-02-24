package com.sbsj.dreamwing.mission.ui

import android.Manifest
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
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.common.model.ErrorResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityWalkBinding
import com.sbsj.dreamwing.mission.model.ActivityType
import com.sbsj.dreamwing.mission.model.request.AwardPointRequest
import com.sbsj.dreamwing.user.ui.LoginActivity
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Converter
import retrofit2.Response
import java.io.IOException

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
    private var previousTotalSteps = 0f
    private lateinit var walk4000Button: Button
    private lateinit var walk7000Button: Button
    private lateinit var walk10000Button: Button
    private lateinit var requestPointButton: Button
    private lateinit var request: AwardPointRequest
    private lateinit var authHeader : String
    private var isLoading = false

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

        updateButtonState(previousTotalSteps)

        val progressPercentage = calculateProgressPercentage(10000, previousTotalSteps.toInt())
        Log.d(TAG, "progress Percentage: ${progressPercentage}")
        binding.progressBar.progress = progressPercentage

        Log.d(TAG, "onCrate previousTotalSteps: ${previousTotalSteps}")

        if (stepCountSensor == null) {
            Toast.makeText(this, "센서가 없습니다.", Toast.LENGTH_SHORT).show()
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACTIVITY_RECOGNITION
                ) == PackageManager.PERMISSION_DENIED
            ) {
                Toast.makeText(this, "권한이 없습니다.", Toast.LENGTH_SHORT).show()
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
     * 로그인 요청 다이얼로그
     */
    private fun showLoginRequestDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_alert, null)
        val confirmTextView = dialogView.findViewById<TextView>(R.id.message)
        val yesButton = dialogView.findViewById<Button>(R.id.yesButton)

        confirmTextView.text = "로그인이 필요합니다."

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        yesButton.setOnClickListener {
            dialog.dismiss()
            // 로그인 액티비티로 이동
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        dialog.show()
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
            val formattedStepCount: String = decimalFormat.format(totalSteps.toInt())

            tvStepCount.text = formattedStepCount
            updateButtonState(totalSteps)
        }
    }

    /**
     * 각 걸음 달성 시 버튼 활성화
     */
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

    /**
     * 4,000 걸음 버튼 클릭 시
     */
    private fun onWalk4000ButtonClick() {
        walk4000Button.setBackgroundResource(R.drawable.bg_round_box_radius_10)
        walk7000Button.setBackgroundResource(R.drawable.selector_walk_button)
        walk10000Button.setBackgroundResource(R.drawable.selector_walk_button)
        requestPointButton.isEnabled = true

        request = AwardPointRequest(
            activityType = ActivityType.WALK_4000.type,
            activityTitle = ActivityType.WALK_4000.title,
            point = ActivityType.WALK_4000.point
        )

    }

    /**
     * 7,000 걸음 버튼 클릭 시
     */
    private fun onWalk7000ButtonClick() {
        walk7000Button.setBackgroundResource(R.drawable.bg_round_box_radius_10)
        walk4000Button.setBackgroundResource(R.drawable.selector_walk_button)
        walk10000Button.setBackgroundResource(R.drawable.selector_walk_button)
        requestPointButton.isEnabled = true

        request = AwardPointRequest(
            activityType = ActivityType.WALK_7000.type,
            activityTitle = ActivityType.WALK_7000.title,
            point = ActivityType.WALK_7000.point
        )
    }

    /**
     * 10,000 걸음 버튼 클릭 시
     */
    private fun onWalk10000ButtonClick() {
        walk10000Button.setBackgroundResource(R.drawable.bg_round_box_radius_10)
        walk7000Button.setBackgroundResource(R.drawable.selector_walk_button)
        walk4000Button.setBackgroundResource(R.drawable.selector_walk_button)
        requestPointButton.isEnabled = true

        request = AwardPointRequest(
            activityType = ActivityType.WALK_10000.type,
            activityTitle = ActivityType.WALK_10000.title,
            point = ActivityType.WALK_10000.point
        )
    }

    private fun onRequestPointButtonClick() {
        awardPoints(request)
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

    /**
     * 포인트 지급 처리
     */
    private fun awardPoints(request: AwardPointRequest) {
        if (checkUserLoggedIn()) {
            // 토큰 가져오기
            val jwtToken = SharedPreferencesUtil.getToken(this)
            authHeader = "$jwtToken" // 헤더에 넣을 변수
            isLoading = true

            RetrofitClient.missionService.awardPoints(authHeader = authHeader, request)
                .enqueue(object :
                    Callback<ApiResponse<Any>> {
                    override fun onResponse(
                        call: Call<ApiResponse<Any>>,
                        response: Response<ApiResponse<Any>>
                    ) {
                        isLoading = false
                        if (response.isSuccessful) {
                            val intent = Intent(this@WalkActivity, WalkAwardActivity::class.java)

                            intent.putExtra("point", request.point)
                            intent.putExtra("activityTitle", request.activityTitle)

                            startActivity(intent)
                        } else {
                            val errorResponse = convertErrorBody(response)
                            val errorMessage = errorResponse?.message ?: "Unknown error"

                            if (errorMessage == "이미 포인트를 받았습니다.") {
                                Log.d("WalkActivity", "$errorResponse")
                                Toast.makeText(
                                    this@WalkActivity,
                                    "이미 포인트를 받았어요!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Log.e("WalkActivity", "서버 오류: $errorResponse")
                            }
                        }
                    }

                    override fun onFailure(call: Call<ApiResponse<Any>>, t: Throwable) {
                        Log.e("QuizActivity", "Request failed: ${t.message}")
                    }
                })
        }
    }

    /**
     * 에러 메세지
     */
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
