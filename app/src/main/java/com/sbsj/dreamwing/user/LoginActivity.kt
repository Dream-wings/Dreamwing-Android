package com.sbsj.dreamwing.user

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sbsj.dreamwing.MainActivity
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.user.model.dto.LoginRequestDTO
import com.sbsj.dreamwing.user.model.response.CheckExistIdResponse
import com.sbsj.dreamwing.user.model.response.SignUpResponse
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 *
 * @author 정은찬
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은찬        최초 생성
 * 2024.08.03   정은찬        Rtrofit을 통해 로그인 정보 보내기 및 JWT 토큰 헤더에서 받기
 */
class LoginActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        val loginId = findViewById<EditText>(R.id.editID)
        val password = findViewById<EditText>(R.id.editPWD)
        val loginButton = findViewById<Button>(R.id.btnLogin)
        val signUpButton = findViewById<Button>(R.id.btnSignUp)

        loginButton.setOnClickListener {
            val loginIdText = loginId.text.toString().trim()
            val passwordText = password.text.toString().trim()

            if (loginIdText.isNotEmpty() && passwordText.isNotEmpty()) {
                login(loginIdText, passwordText)
            } else {
                showErrorDialog()
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun login(loginId: String, password: String) {
        // 요청 바디 생성
        val loginRequestDTO = LoginRequestDTO(loginId, password)

        RetrofitClient.userService.login(loginRequestDTO).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 성공적으로 로그인한 경우 Authorization 헤더에서 JWT 토큰 추출
                    val token = response.headers()["Authorization"]
                    if (token != null) {
                        showLoginSuccessDialog()
                    } else {
                        Toast.makeText(this@LoginActivity, "토큰을 가져오지 못했습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // 로그인 실패 시 사용자에게 알림
                    showLoginFailDialog()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 요청 실패 시 사용자에게 알림
                Toast.makeText(this@LoginActivity, "로그인 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * JWT 토큰을 SharedPreferences에 저장하는 함수
     * @param token JWT 토큰
     */
    private fun saveToken(token: String) {
        val sharedPreferences = getSharedPreferences("auth", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("token", token)
        editor.apply()
    }

    // 오류 다이얼로그를 표시하는 메서드
    private fun showErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("오류")
            .setMessage("로그인 정보를 입력해주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // 로그인 성공 다이얼로그를 표시하는 메서드
    private fun showLoginSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그인 성공")
            .setMessage("로그인이 성공적으로 완료되었습니다.")
            .setPositiveButton("확인") { dialog, _ ->
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            }
            .show()
    }

    // 로그인 실패 다이얼로그를 표시하는 메서드
    private fun showLoginFailDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그인 실패")
            .setMessage("로그인 정보를 다시 입력해주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}