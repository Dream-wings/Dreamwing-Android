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
import java.text.NumberFormat
import java.util.Locale

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
                        val numberFormat = NumberFormat.getNumberInstance(Locale.getDefault())
                        val formattedTotalPoints = numberFormat.format(it.totalPoints)
                        totalPoint.text = formattedTotalPoints

                        val totalPointAnimation = it.totalPoints
                        startAnimation(totalPointAnimation)
                    }
                } else {
                    Log.e("SplashActivity", "Response not successful")
                    totalPoint.text = "0"
                }
            }

            override fun onFailure(call: Call<ApiResponse<TotalSupportResponse>>, t: Throwable) {
                Log.e("SplashActivity", "Error: ${t.message}")
                totalPoint.text = "0"
            }
        })
    }

    /**
     * 애니메이션 시작 메서드
     */
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
