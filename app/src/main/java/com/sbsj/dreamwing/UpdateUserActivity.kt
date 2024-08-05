package com.sbsj.dreamwing

import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.imageview.ShapeableImageView
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.user.LoginActivity
import com.sbsj.dreamwing.user.model.response.CheckExistIdResponse
import com.sbsj.dreamwing.user.model.response.SignUpResponse
import com.sbsj.dreamwing.user.model.response.UpdateResponse
import com.sbsj.dreamwing.util.SharedPreferencesUtil
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.InputStream

class UpdateUserActivity : AppCompatActivity() {
    private var imageUri: Uri? = null // 이미지를 선택한 후의 URI를 저장하는 변수
    private lateinit var idCheckMessage: TextView // 아이디 중복 확인 메시지를 표시하는 TextView
    private lateinit var passwordMatchMessage: TextView // 비밀번호 일치 여부를 표시하는 TextView
    private var idCheckStatus = false // 아이디 중복 여부 상태, 초기값은 중복
    private var passwordMatchStatus = false // 비밀번호 일치 여부 상태, 초기값은 불일치

    // 각 입력 필드 밑에 있는 밑줄 View를 위한 변수 선언
    private lateinit var underlineID: View
    private lateinit var underlinePWD: View
    private lateinit var underlinePWDConfirm: View
    private lateinit var underlineName: View
    private lateinit var underlinePhone: View

    // 이미지를 선택하는 ActivityResultLauncher 설정
    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            imageUri = result.data?.data // 이미지 URI를 가져옴
            imageUri?.let {
                val imageFile = createImageFile(it) // 이미지 파일 생성
                findViewById<ShapeableImageView>(R.id.registration_iv).setImageURI(imageUri) // ImageView에 선택한 이미지 표시
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_user)

        // UI 요소 초기화
        val loginId = findViewById<EditText>(R.id.editID)
        val password = findViewById<EditText>(R.id.editPWD)
        val passwordConfirm = findViewById<EditText>(R.id.editPWDConfirm)
        val name = findViewById<EditText>(R.id.editName)
        val phone = findViewById<EditText>(R.id.editPhone)
        val checkExistIDButton = findViewById<Button>(R.id.btnCheckExistID)
        val updateButton = findViewById<Button>(R.id.btnDone)
        val profile = findViewById<ShapeableImageView>(R.id.registration_iv)
        idCheckMessage = findViewById(R.id.idCheckMessage) // 아이디 중복 확인 메시지를 표시하는 TextView
        passwordMatchMessage = findViewById(R.id.passwordMatchMessage) // 비밀번호 일치 여부 메시지를 표시하는 TextView

        // 밑줄 View 초기화
        underlineID = findViewById(R.id.underlineID)
        underlinePWD = findViewById(R.id.underlinePWD)
        underlinePWDConfirm = findViewById(R.id.underlinePWDConfirm)
        underlineName = findViewById(R.id.underlineName)
        underlinePhone = findViewById(R.id.underlinePhone)

        // 프로필 이미지 클릭 시 갤러리에서 이미지 선택
        profile.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }

        // 중복 확인 버튼 클릭 시 실행되는 코드
        checkExistIDButton.setOnClickListener {
            val idText = loginId.text.toString()
            checkExistLoginId(idText) // 아이디 중복 확인 요청
        }

        // 회원가입 버튼 클릭 시 실행되는 코드
        updateButton.setOnClickListener {
            val idText = loginId.text.toString()
            val passwordText = password.text.toString()
            val nameText = name.text.toString()
            val phoneText = phone.text.toString()

            // 입력된 값이 비어있는지 확인
            if (idText.isEmpty() || passwordText.isEmpty() || nameText.isEmpty() || phoneText.isEmpty()) {
                showErrorDialog("빈 입력칸이 있습니다.")
            } else if (!idCheckStatus) {
                // 중복 확인이 안 되어 있으면 다이얼로그 표시
                showIdCheckDialog(idText)
            } else if (!passwordMatchStatus) {
                showErrorDialog("비밀번호가 일치하지 않습니다.")
            } else {
                val imageFile = imageUri?.let { createImageFile(it) } // 이미지 파일 생성

                val requestFile = imageFile?.asRequestBody("image/png".toMediaTypeOrNull())
                val imagePart = requestFile?.let { MultipartBody.Part.createFormData("imageFile", imageFile.name, it) }

                // 입력된 값을 RequestBody로 변환
                val loginIdRequestBody = idText.toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordRequestBody = passwordText.toRequestBody("text/plain".toMediaTypeOrNull())
                val nameRequestBody = nameText.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneRequestBody = phoneText.toRequestBody("text/plain".toMediaTypeOrNull())
                val profileImageUrlRequestBody = null

                // 회원 정보 수정 요청
                update(loginIdRequestBody, passwordRequestBody, nameRequestBody, phoneRequestBody, imagePart, profileImageUrlRequestBody)
            }
        }

        // EditText에 TextWatcher 및 OnFocusChangeListener 설정
        setEditTextListeners(loginId, underlineID)
        setEditTextListeners(password, underlinePWD)
        setEditTextListeners(passwordConfirm, underlinePWDConfirm)
        setEditTextListeners(name, underlineName)
        setEditTextListeners(phone, underlinePhone)

        // 비밀번호와 비밀번호 확인란에 TextWatcher 추가
        setPasswordMatchListener(password, passwordConfirm)
    }

    // EditText에 TextWatcher와 OnFocusChangeListener를 설정하는 메서드
    private fun setEditTextListeners(editText: EditText, underline: View) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // 아이디 입력란의 텍스트가 변경되면 idCheckMessage 숨기기
                if (editText.id == R.id.editID) {
                    idCheckMessage.visibility = View.GONE
                    idCheckStatus = false // 중복 확인 완료 상태를 초기화
                }
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                // 포커스를 얻으면 배경색 변경
                underline.setBackgroundColor(getColor(R.color.skyblue))
            } else {
                // 포커스를 잃으면 배경색 변경
                underline.setBackgroundColor(getColor(R.color.black))
            }
        }
    }

    // 비밀번호와 비밀번호 확인란이 일치하는지 확인하는 TextWatcher를 설정하는 메서드
    private fun setPasswordMatchListener(password: EditText, passwordConfirm: EditText) {
        val passwordTextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validatePasswordMatch(password, passwordConfirm)
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        password.addTextChangedListener(passwordTextWatcher)
        passwordConfirm.addTextChangedListener(passwordTextWatcher)
    }

    // 비밀번호와 비밀번호 확인란의 일치 여부를 검증하고 메시지를 업데이트하는 메서드
    private fun validatePasswordMatch(password: EditText, passwordConfirm: EditText) {
        val passwordText = password.text.toString()
        val confirmText = passwordConfirm.text.toString()

        // 비밀번호 확인란이 비어 있는지 검사
        if (confirmText.isEmpty()) {
            passwordMatchMessage.visibility = View.GONE
            passwordMatchStatus = false // 비밀번호 불일치 상태로 설정
        } else {
            // 비밀번호와 비밀번호 확인란의 값을 비교
            if (confirmText == passwordText) {
                passwordMatchMessage.text = "비밀번호가 일치합니다."
                passwordMatchMessage.setTextColor(getColor(R.color.green))
                passwordMatchStatus = true // 비밀번호 일치 상태로 설정
            } else {
                passwordMatchMessage.text = "비밀번호가 일치하지 않습니다."
                passwordMatchMessage.setTextColor(getColor(R.color.red))
                passwordMatchStatus = false // 비밀번호 불일치 상태로 설정
            }
            passwordMatchMessage.visibility = View.VISIBLE
        }
    }

    // 이미지 URI로부터 파일을 생성하는 메서드
    private fun createImageFile(uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "profile_image_${System.currentTimeMillis()}.png")
        inputStream?.use {
            try {
                it.copyTo(file.outputStream())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    // 아이디 중복 확인을 위한 네트워크 요청 메서드
    private fun checkExistLoginId(loginId: String) {
        RetrofitClient.userService.checkExistLoginId(loginId).enqueue(object :
            Callback<CheckExistIdResponse> {
            override fun onResponse(call: Call<CheckExistIdResponse>, response: Response<CheckExistIdResponse>) {
                if (response.isSuccessful) {
                    val checkExistIdResponse = response.body()
                    if (checkExistIdResponse != null) {
                        if (checkExistIdResponse.data) {
                            // 아이디 사용 가능
                            idCheckMessage.text = "사용 가능한 아이디입니다."
                            idCheckMessage.setTextColor(getColor(R.color.green))
                            idCheckStatus = true // 아이디 중복이 아님
                        } else {
                            // 아이디 사용 불가능
                            idCheckMessage.text = "사용할 수 없는 아이디입니다."
                            idCheckMessage.setTextColor(getColor(R.color.red))
                            idCheckStatus = false // 아이디 중복
                        }
                        idCheckMessage.visibility = View.VISIBLE
                    } else {
                        Toast.makeText(this@UpdateUserActivity, "응답이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UpdateUserActivity, "중복 확인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CheckExistIdResponse>, t: Throwable) {
                Toast.makeText(this@UpdateUserActivity, "중복 확인 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 회원가입 요청을 서버로 보내는 메서드
    private fun update(
        loginId: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        imageFile: MultipartBody.Part?,
        profileImageUrl: RequestBody?
    ) {
        // SharedPreferencesUtil을 사용하여 JWT 토큰 가져오기
        val jwtToken = SharedPreferencesUtil.getToken(this)
        if (jwtToken.isNullOrEmpty()) {
            // JWT 토큰이 없으면 데이터를 로드하지 않음
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show()
            return
        }

        val authHeader = "$jwtToken"


        RetrofitClient.userService.update(authHeader, loginId, password, name, phone, imageFile, profileImageUrl).enqueue(object : Callback<UpdateResponse> {
            override fun onResponse(call: Call<UpdateResponse>, response: Response<UpdateResponse>) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse != null) {
                        if (signUpResponse.success) {
                            updateSuccessDialog()
                        } else {
                            Toast.makeText(this@UpdateUserActivity, "회원 정보 수정 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@UpdateUserActivity, "응답이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@UpdateUserActivity, "회원 정보 수정 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<UpdateResponse>, t: Throwable) {
                Toast.makeText(this@UpdateUserActivity, "회원 정보 수정 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 아이디 중복 확인 다이얼로그를 띄우는 메서드
    private fun showIdCheckDialog(id: String) {
        AlertDialog.Builder(this)
            .setTitle("아이디 중복 확인")
            .setMessage("아이디 중복 확인이 필요합니다.")
            .setPositiveButton("확인") {  dialog, _ -> dialog.dismiss()
            }
            .show()
    }

    // 오류 다이얼로그를 표시하는 메서드
    private fun showErrorDialog(message: String) {
        AlertDialog.Builder(this)
            .setTitle("오류")
            .setMessage(message)
            .setPositiveButton("확인") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    // 회원가입 수정 다이얼로그를 표시하는 메서드
    private fun updateSuccessDialog() {
        AlertDialog.Builder(this)
            .setTitle("회원 정보 수정 완료")
            .setMessage("회원 정보 수정이 성공적으로 완료되었습니다.")
            .setPositiveButton("확인") { dialog, _ ->
                // 확인 버튼 클릭 시 로그인 화면으로 이동
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish() // 현재 액티비티 종료
            }
            .show()
    }
}