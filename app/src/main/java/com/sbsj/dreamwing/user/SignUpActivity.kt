package com.sbsj.dreamwing.user

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
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textfield.TextInputLayout
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.user.model.response.CheckExistIdResponse
import com.sbsj.dreamwing.user.model.response.SignUpResponse
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

class SignUpActivity : AppCompatActivity() {
    private var imageUri: Uri? = null // 이미지를 선택한 후의 URI를 저장하는 변수
    private lateinit var idCheckMessage: TextView // 아이디 중복 확인 메시지를 표시하는 TextView

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
        setContentView(R.layout.activity_signup)

        // UI 요소 초기화
        val loginId = findViewById<EditText>(R.id.editID)
        val password = findViewById<EditText>(R.id.editPWD)
        val passwordConfirm = findViewById<EditText>(R.id.editPWDConfirm)
        val name = findViewById<EditText>(R.id.editName)
        val phone = findViewById<EditText>(R.id.editPhone)
        val checkExistIDButton = findViewById<Button>(R.id.btnCheckExistID)
        val signUpButton = findViewById<Button>(R.id.btnDone)
        val profile = findViewById<ShapeableImageView>(R.id.registration_iv)
        idCheckMessage = findViewById(R.id.idCheckMessage) // 아이디 중복 확인 메시지를 표시하는 TextView

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
        signUpButton.setOnClickListener {
            val idText = loginId.text.toString()
            val passwordText = password.text.toString()
            val nameText = name.text.toString()
            val phoneText = phone.text.toString()

            // 입력된 값이 비어있는지 확인
            if (idText.isEmpty() || passwordText.isEmpty() || nameText.isEmpty() || phoneText.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호, 이름, 전화번호를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
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

                // 회원가입 요청
                signUp(loginIdRequestBody, passwordRequestBody, nameRequestBody, phoneRequestBody, imagePart, profileImageUrlRequestBody)
            }
        }

        // EditText에 TextWatcher 및 OnFocusChangeListener 설정
        setEditTextListeners(loginId)
        setEditTextListeners(password)
        setEditTextListeners(passwordConfirm)
        setEditTextListeners(name)
        setEditTextListeners(phone)
    }

    // EditText에 TextWatcher와 OnFocusChangeListener를 설정하는 메서드
    private fun setEditTextListeners(editText: EditText) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (editText.hasFocus()) {
                    editText.backgroundTintList = getColorStateList(R.color.skyblue) // 텍스트 입력 시 배경색 변경
                }
                idCheckMessage.visibility = View.GONE // 입력값이 수정되면 메시지 숨기기
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        editText.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                editText.backgroundTintList = getColorStateList(R.color.skyblue) // 포커스를 얻으면 배경색 변경
            } else {
                editText.backgroundTintList = getColorStateList(R.color.black) // 포커스를 잃으면 배경색 변경
            }
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
        RetrofitClient.userService.checkExistLoginId(loginId).enqueue(object : Callback<CheckExistIdResponse> {
            override fun onResponse(call: Call<CheckExistIdResponse>, response: Response<CheckExistIdResponse>) {
                if (response.isSuccessful) {
                    val checkExistIdResponse = response.body()
                    if (checkExistIdResponse != null) {
                        if (checkExistIdResponse.data) {
                            // 아이디 사용 가능
                            idCheckMessage.text = "사용 가능한 아이디입니다."
                            idCheckMessage.setTextColor(getColor(R.color.green))
                            idCheckMessage.visibility = View.VISIBLE
                        } else {
                            // 아이디 사용 불가능
                            idCheckMessage.text = "사용할 수 없는 아이디입니다."
                            idCheckMessage.setTextColor(getColor(R.color.red))
                            idCheckMessage.visibility = View.VISIBLE
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "응답이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignUpActivity, "중복 확인 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CheckExistIdResponse>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "중복 확인 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 회원가입 요청을 서버로 보내는 메서드
    private fun signUp(
        loginId: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        imageFile: MultipartBody.Part?,
        profileImageUrl: RequestBody?
    ) {
        RetrofitClient.userService.signUp(loginId, password, name, phone, imageFile, profileImageUrl).enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(call: Call<SignUpResponse>, response: Response<SignUpResponse>) {
                if (response.isSuccessful) {
                    val signUpResponse = response.body()
                    if (signUpResponse != null) {
                        if (signUpResponse.success) {
                            Toast.makeText(this@SignUpActivity, "회원가입 성공", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(this@SignUpActivity, "회원가입 실패", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@SignUpActivity, "응답이 비어있습니다.", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@SignUpActivity, "회원가입 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                Toast.makeText(this@SignUpActivity, "회원가입 실패: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}