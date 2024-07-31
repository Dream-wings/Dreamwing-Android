package com.sbsj.dreamwing

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.support.model.response.TotalSupportResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 스플래시 스크린
 * @author 정은지
 * @since 2024.07.31
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.07.31   정은지        최초 생성
 */
class SplashActivity : AppCompatActivity() {

    private lateinit var totalPoint: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        totalPoint = findViewById(R.id.total_support)

        RetrofitClient.supportService.getTotalSupport().enqueue(object : Callback<ApiResponse<TotalSupportResponse>> {
            override fun onResponse(call: Call<ApiResponse<TotalSupportResponse>>, response: Response<ApiResponse<TotalSupportResponse>>) {
                if (response.isSuccessful) {
                    val support = response.body()?.data
                    Log.d("SplashActivity", "TotalSupport: $support")
                    support?.let {
                        totalPoint.text = it.totalPoints.toString()
                        val totalP = it.totalPoints
                        startAnimation(totalP)
                    }
                } else {
                    Log.e("SplashActivity", "Response not successful")
                    totalPoint.text = "Failed"
                }
            }

            override fun onFailure(call: Call<ApiResponse<TotalSupportResponse>>, t: Throwable) {
                Log.e("SplashActivity", "Error: ${t.message}")
                totalPoint.text = "Failed"
            }
        })
    }

    private fun startAnimation(endValue: Int) {
        val animator = ValueAnimator.ofInt(0, endValue)
        animator.duration = 3000
        animator.addUpdateListener { animation ->
            totalPoint.text = animation.animatedValue.toString()
        }
        animator.addListener(object : AnimatorListenerAdapter() {

            override fun onAnimationEnd(animation: Animator) {
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        })
        animator.start()
    }
}
