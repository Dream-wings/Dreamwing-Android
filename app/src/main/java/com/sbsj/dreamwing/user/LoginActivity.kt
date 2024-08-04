package com.sbsj.dreamwing.user

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sbsj.dreamwing.MainActivity
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityLoginBinding
import com.sbsj.dreamwing.user.model.dto.LoginRequestDTO
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 로그인 액티비티
 * 사용자 로그인 요청을 처리하고 JWT 토큰을 저장합니다.
 * @author 정은찬
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은찬        최초 생성
 * 2024.08.03   정은찬        Retrofit을 통해 로그인 정보 보내기 및 JWT 토큰 헤더에서 받기
 * 2024.08.03   정은찬        뒤로가기 툴바 적용
 */
class LoginActivity : AppCompatActivity() {

    // ViewBinding 객체를 사용하여 XML 레이아웃과 연결
    private lateinit var binding: ActivityLoginBinding

    /**
     * 액티비티가 생성될 때 호출됩니다.
     * 화면에 뷰를 설정하고, 액티비티의 초기화 작업을 수행합니다.
     * @param savedInstanceState 액티비티의 상태를 저장한 번들 객체
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ActivityLoginBinding을 사용하여 레이아웃을 설정
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        // 툴바를 설정하고 제목을 "로그인"으로 설정
        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "로그인"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 뷰 참조 가져오기
        val loginId = binding.editID
        val password = binding.editPWD
        val loginButton = binding.btnLogin
        val signUpButton = binding.btnSignUp

        // 로그인 버튼 클릭 리스너 설정
        loginButton.setOnClickListener {
            // 입력된 아이디와 비밀번호를 가져오기
            val loginIdText = loginId.text.toString().trim()
            val passwordText = password.text.toString().trim()

            // 아이디와 비밀번호가 모두 입력되었는지 확인
            if (loginIdText.isNotEmpty() && passwordText.isNotEmpty()) {
                // 로그인 메서드 호출
                login(loginIdText, passwordText)
            } else {
                // 입력되지 않은 경우 오류 다이얼로그 표시
                showErrorDialog()
            }
        }

        // 회원가입 버튼 클릭 리스너 설정
        signUpButton.setOnClickListener {
            // 회원가입 액티비티로 이동
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            finish() // 현재 액티비티 종료
        }
    }

    /**
     * 사용자의 로그인 요청을 처리합니다.
     * @param loginId 사용자 아이디
     * @param password 사용자 비밀번호
     */
    private fun login(loginId: String, password: String) {
        // 로그인 요청 DTO 생성
        val loginRequestDTO = LoginRequestDTO(loginId, password)

        // Retrofit을 사용하여 로그인 요청
        RetrofitClient.userService.login(loginRequestDTO).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                // 요청이 성공적으로 완료되었는지 확인
                if (response.isSuccessful) {
                    // Authorization 헤더에서 JWT 토큰 추출
                    val token = response.headers()["Authorization"]
                    if (token != null) {
                        // 로그인 성공 다이얼로그 표시
                        showLoginSuccessDialog()
                        // 토큰 저장 (추후 사용 가능)
                        SharedPreferencesUtil.saveToken(this@LoginActivity, token)
                    } else {
                        // 토큰을 가져오지 못한 경우 사용자에게 알림
                        showNetworkFailDialog()
                    }
                } else {
                    // 로그인 실패 시 사용자에게 알림
                    showLoginFailDialog()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                // 네트워크 요청 실패 시 사용자에게 알림
                showNetworkFailDialog()
            }
        })
    }

    /**
     * 로그인 정보를 입력하지 않았을 때 오류 다이얼로그를 표시합니다.
     */
    private fun showErrorDialog() {
        AlertDialog.Builder(this)
            .setTitle("오류")
            .setMessage("로그인 정보를 입력해주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * 로그인 성공 시 성공 다이얼로그를 표시하고, MainActivity로 이동합니다.
     */
    private fun showLoginSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그인 성공")
            .setMessage("로그인이 성공적으로 완료되었습니다.")
            .setPositiveButton("확인") { dialog, _ ->
                // MainActivity로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            }
            .show()
    }

    /**
     * 로그인 실패 시 실패 다이얼로그를 표시합니다.
     */
    private fun showLoginFailDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그인 실패")
            .setMessage("로그인 정보를 다시 입력해주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    /**
     * 네트워크 오류 다이얼로그를 표시합니다.
     */
    private fun showNetworkFailDialog() {
        AlertDialog.Builder(this)
            .setTitle("로그인 실패")
            .setMessage("나중에 다시 시도해주세요.")
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }
}
