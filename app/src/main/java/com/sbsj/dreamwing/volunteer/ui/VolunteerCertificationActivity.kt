package com.sbsj.dreamwing.volunteer.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.gson.Gson
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient.volunteerService
import com.sbsj.dreamwing.databinding.ActivityVolunteerCertificationBinding
import com.sbsj.dreamwing.mission.ui.QuizCorrectActivity
import com.sbsj.dreamwing.volunteer.model.request.CertificationVolunteerRequest
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.io.InputStream

/**
 * 봉사활동 인증 화면
 * @author 정은지
 * @since 2024.08.03
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.03   정은지        최초 생성
 */
class VolunteerCertificationActivity : AppCompatActivity() {

    private lateinit var binding: ActivityVolunteerCertificationBinding
    private var imageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVolunteerCertificationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar.root)
        supportActionBar?.title = "봉사활동 인증"
        val volunteerTitle = intent.getStringExtra("volunteerTitle")
        binding.comment1.text = volunteerTitle ?: "봉사활동 제목"

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 권한 요청
        requestPermission()

        binding.activityMainButtonGallery.setOnClickListener {
            selectImageFromGallery()
        }

        binding.uploadImage.setOnClickListener {
            imageUri?.let { uri ->
                val imageFile = createImageFile(uri)
                uploadImage(imageFile)
            } ?: Toast.makeText(this, "이미지를 선택하세요.", Toast.LENGTH_SHORT).show()
        }
    }

    // 툴바 뒤로가기 버튼
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("VolunteerCertificationActivity", "권한 설정되어 있음")
            } else {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    1
                )
            }
        }
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                imageUri = data.data
                imageUri?.let {
                    binding.previewImage.setImageURI(it)
                }
            }
        } else {
            Toast.makeText(this, "이미지 선택 실패", Toast.LENGTH_SHORT).show()
        }
    }

    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getContent.launch(intent)
    }

    private fun createImageFile(uri: Uri): File {
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val file = File(cacheDir, "img_${System.currentTimeMillis()}.png")
        inputStream?.use {
            try {
                it.copyTo(file.outputStream())
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
        return file
    }

    private fun uploadImage(file: File) {
        val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
        val imagePart = MultipartBody.Part.createFormData("imageFile", file.name, requestFile)

        val requestDTO = CertificationVolunteerRequest(
            userId = 3,
            volunteerId = 1,
            imageUrl = null,
            imageFile = null
        )

        val requestJson = Gson().toJson(requestDTO)
        val requestBody = requestJson.toRequestBody("application/json".toMediaTypeOrNull())

        val call = volunteerService.certificationVolunteer(requestBody, imagePart)
        call.enqueue(object : Callback<ApiResponse<Void>> {
            override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                if (response.isSuccessful) {
                    val intent = Intent(this@VolunteerCertificationActivity,
                        VolunteerCertificationSuccessActivity::class.java)
                    startActivity(intent)
                } else {
                    Toast.makeText(this@VolunteerCertificationActivity, "업로드 실패: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                Toast.makeText(this@VolunteerCertificationActivity, "업로드 에러: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
