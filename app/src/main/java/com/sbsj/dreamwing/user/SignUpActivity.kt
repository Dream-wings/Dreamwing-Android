package com.sbsj.dreamwing.user

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.imageview.ShapeableImageView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.user.model.response.SignUpResponse
import com.sbsj.dreamwing.user.service.UserService
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.IOException
import java.io.InputStream


class SignUpActivity : AppCompatActivity() {
    private var imageUri: Uri? = null
    private lateinit var apiService: UserService

    private val getContent =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                imageUri = result.data?.data
                imageUri?.let {
                    val imageFile = createImageFile(it)
                    findViewById<ShapeableImageView>(R.id.registration_iv).setImageURI(imageUri)
                    Log.d("이미지", "성공")
                }
            } else {
                Log.d("이미지", "실패")
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Retrofit 설정
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl("http://10.0.2.2")  // 서버 URL을 확인하세요
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(UserService::class.java)

        val loginId = findViewById<EditText>(R.id.editID)
        val password = findViewById<EditText>(R.id.editPWD)
        val name = findViewById<EditText>(R.id.editName)
        val phone = findViewById<EditText>(R.id.editPhone)
        val signUpButton = findViewById<Button>(R.id.btnDone)
        val profile = findViewById<ShapeableImageView>(R.id.registration_iv)

        profile.setOnClickListener {
            val intentImage = Intent(Intent.ACTION_PICK)
            intentImage.type = MediaStore.Images.Media.CONTENT_TYPE
            getContent.launch(intentImage)
        }

        signUpButton.setOnClickListener {
            val idText = loginId.text.toString()
            val passwordText = password.text.toString()
            val nameText = name.text.toString()
            val phoneText = phone.text.toString()

            if (idText.isEmpty() || passwordText.isEmpty() || nameText.isEmpty() || phoneText.isEmpty()) {
                Toast.makeText(this, "아이디와 비밀번호, 이름, 전화번호를 제대로 입력해주세요.", Toast.LENGTH_SHORT).show()
            } else {
                val imageFile = imageUri?.let { createImageFile(it) }

                val requestFile = imageFile?.asRequestBody("image/png".toMediaTypeOrNull())
                val imagePart = requestFile?.let { MultipartBody.Part.createFormData("imageFile", imageFile.name, it) }

                val loginIdRequestBody = idText.toRequestBody("text/plain".toMediaTypeOrNull())
                val passwordRequestBody = passwordText.toRequestBody("text/plain".toMediaTypeOrNull())
                val nameRequestBody = nameText.toRequestBody("text/plain".toMediaTypeOrNull())
                val phoneRequestBody = phoneText.toRequestBody("text/plain".toMediaTypeOrNull())
                val profileImageUrlRequestBody = null // 프로필 이미지 URL이 없다면 null로 설정

                signUp(loginIdRequestBody, passwordRequestBody, nameRequestBody, phoneRequestBody, imagePart, profileImageUrlRequestBody)
            }
        }
    }

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

    private fun signUp(
        loginId: RequestBody,
        password: RequestBody,
        name: RequestBody,
        phone: RequestBody,
        imageFile: MultipartBody.Part?,
        profileImageUrl: RequestBody?
    ) {
        val call = apiService.signUp(loginId, password, name, phone, imageFile, profileImageUrl)
        call.enqueue(object : Callback<SignUpResponse> {
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