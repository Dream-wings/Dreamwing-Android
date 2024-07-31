package com.sbsj.dreamwing.mission

import android.Manifest
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.getSystemService
import com.sbsj.dreamwing.R

class MissionActivity : AppCompatActivity() , SensorEventListener {
    private lateinit var sensorManager: SensorManager
    private var stepCountSensor: Sensor? = null
    private lateinit var tvStepCount: TextView

    //    private val TYPE = Sensor.TYPE_STEP_DETECTOR //보행 감지기
    private val TYPE = Sensor.TYPE_STEP_COUNTER //보행 계수기

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mission)

        tvStepCount = findViewById(R.id.tvStepCount)
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        stepCountSensor = sensorManager.getDefaultSensor(TYPE)
        if(stepCountSensor == null) {
            Toast.makeText(this, "No Step Detect Sensor!!", Toast.LENGTH_SHORT).show()
        }else{
            if(ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACTIVITY_RECOGNITION) == PackageManager.PERMISSION_DENIED){
                Toast.makeText(this, "No Permission!!", Toast.LENGTH_SHORT).show()

                //ask for permission
                requestPermissionLauncher.launch(arrayOf(Manifest.permission.ACTIVITY_RECOGNITION))

            }else{
                //권한있는 경우
            }
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()){
        Log.d(TAG, "requestPermissionLauncher: 건수 : ${it.size}")

        var results = true
        it.values.forEach{
            if(it == false) {
                results = false
                return@forEach
            }
        }

        if(!results){
            Toast.makeText(this@MissionActivity, "권한이 필요합니다.", Toast.LENGTH_SHORT).show()
        }else{
            //모두 권한이 있을 경우
        }
    }

    override fun onSensorChanged(p0: SensorEvent?) {
        if(p0?.sensor?.type == TYPE) {
            Log.d(TAG, "onSensorChanged: ${p0.values[0]}")
            tvStepCount.text = "Step Count Sensor : " + p0.values[0].toString()
        }
    }

    override fun onAccuracyChanged(p0: Sensor?, p1: Int) {
    }

    override fun onResume() {
        super.onResume()
        var bool = sensorManager.registerListener(this, stepCountSensor, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }
}