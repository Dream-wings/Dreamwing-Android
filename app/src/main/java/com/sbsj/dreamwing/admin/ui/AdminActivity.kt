package com.sbsj.dreamwing.admin.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.admin.adapter.AdminItemAdapter
import com.sbsj.dreamwing.admin.model.response.AdminVolunteerDTO
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListDTO
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.ActivityAdminBinding
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding

    private lateinit var volunteerAdapter: AdminItemAdapter
    private val volunteerList: MutableList<VolunteerAdminListDTO> = mutableListOf()
    private var volunteerPage = 0
    private val pageSize = 10

    private var selectedVolunteerId: Long? = null // 선택된 봉사 ID
    private var selectedImageUri: Uri? = null
    private lateinit var dialogView: View

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.adminToolbar.toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        binding.adminToolbar.toolbar.title = "봉사활동 게시판 관리"
        // RecyclerView 초기화
        setupRecyclerView()

        // 초기 데이터 로드
        loadMoreVolunteers()

        // 등록 버튼 클릭 리스너 설정
        binding.addButton.setOnClickListener {
            showAddVolunteerDialog()
        }

        // 수정 버튼 클릭 리스너 설정
        binding.editButton.setOnClickListener {
            selectedVolunteerId?.let {
                loadVolunteerDetail(it) // 봉사 상세 정보 로드 후 수정 다이얼로그 표시
            } ?: run {
                Toast.makeText(this, "수정할 항목을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }

        // 삭제 버튼 클릭 리스너 설정
        binding.deleteButton.setOnClickListener {
            selectedVolunteerId?.let {
                showDeleteConfirmationDialog(it)
            } ?: run {
                Toast.makeText(this, "삭제할 항목을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.volunteer_board -> {
                startActivity(Intent(this, AdminActivity::class.java))
                return true
            }
            R.id.volunteer_request -> {
                startActivity(Intent(this, VolunteerRequestListActivity::class.java))
                return true
            }
            R.id.volunteer_certification -> {
                startActivity(Intent(this, VolunteerCertificationListActivity::class.java))
                return true
            }
            else -> return super.onOptionsItemSelected(item)
        }
    }


    private fun setupRecyclerView() {
        volunteerAdapter = AdminItemAdapter(volunteerList) { volunteerId ->
            selectedVolunteerId = volunteerId
            Toast.makeText(this, "선택된 ID: $volunteerId", Toast.LENGTH_SHORT).show()
        }
        binding.recyclerView.adapter = volunteerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreVolunteers()
                }
            }
        })
    }

    private fun loadMoreVolunteers() {
        RetrofitClient.volunteerService.getVolunteerList(volunteerPage, pageSize)
            .enqueue(object : Callback<VolunteerAdminListResponse> {
                override fun onResponse(
                    call: Call<VolunteerAdminListResponse>,
                    response: Response<VolunteerAdminListResponse>
                ) {
                    if (response.isSuccessful) {
                        response.body()?.let { volunteerAdminListResponse ->
                            if (volunteerAdminListResponse.data.isNotEmpty()) {
                                volunteerList.addAll(volunteerAdminListResponse.data)
                                volunteerAdapter.notifyDataSetChanged()
                                volunteerPage++
                            }
                        }
                    } else {
                        Toast.makeText(this@AdminActivity, "봉사 목록을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VolunteerAdminListResponse>, t: Throwable) {
                    Toast.makeText(this@AdminActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun refreshVolunteerList() {
        volunteerPage = 0
        volunteerList.clear()
        loadMoreVolunteers()
    }

    private fun showAddVolunteerDialog() {
        dialogView = layoutInflater.inflate(R.layout.dialog_add_volunteer, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("봉사 등록")

        val dialog = builder.create()

        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = dialogView.findViewById<EditText>(R.id.editTextContent)
        val editTextType = dialogView.findViewById<EditText>(R.id.editTextType)
        val editTextCategory = dialogView.findViewById<EditText>(R.id.editTextCategory)
        val editTextVolunteerStartDate = dialogView.findViewById<EditText>(R.id.editTextVolunteerStartDate)
        val editTextVolunteerEndDate = dialogView.findViewById<EditText>(R.id.editTextVolunteerEndDate)
        val editTextAddress = dialogView.findViewById<EditText>(R.id.editTextAddress)
        val editTextTotalCount = dialogView.findViewById<EditText>(R.id.editTextTotalCount)
        val editTextStatus = dialogView.findViewById<EditText>(R.id.editTextStatus)
        val editTextRecruitStartDate = dialogView.findViewById<EditText>(R.id.editTextRecruitStartDate)
        val editTextRecruitEndDate = dialogView.findViewById<EditText>(R.id.editTextRecruitEndDate)
        val editTextLatitude = dialogView.findViewById<EditText>(R.id.editTextLatitude)
        val editTextLongitude = dialogView.findViewById<EditText>(R.id.editTextLongitude)
        val buttonSelectImage = dialogView.findViewById<Button>(R.id.buttonSelectImage)
        val imageViewPreview = dialogView.findViewById<ImageView>(R.id.imageViewPreview)
        val buttonAddVolunteer = dialogView.findViewById<Button>(R.id.buttonAddVolunteer)

        buttonSelectImage.setOnClickListener {
            openImagePicker()
        }

        buttonAddVolunteer.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            val type = editTextType.text.toString().toIntOrNull() ?: 0
            val category = editTextCategory.text.toString().toIntOrNull() ?: 0

            // 사용자가 입력한 날짜를 LocalDateTime으로 변환한 후 형식에 맞춰 문자열로 변환
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

            val volunteerStartDate = editTextVolunteerStartDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }
            val volunteerEndDate = editTextVolunteerEndDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }
            val recruitStartDate = editTextRecruitStartDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }
            val recruitEndDate = editTextRecruitEndDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }

            val address = editTextAddress.text.toString()
            val totalCount = editTextTotalCount.text.toString().toIntOrNull() ?: 0
            val status = editTextStatus.text.toString().toIntOrNull() ?: 0

            val imageUrl = selectedImageUri?.toString()
            val latitude = editTextLatitude.text.toString().toDoubleOrNull()
            val longitude = editTextLongitude.text.toString().toDoubleOrNull()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val volunteer = AdminVolunteerDTO(
                    volunteerId = 0L, // Assuming 0L is a placeholder ID for new entries
                    title = title,
                    content = content,
                    type = type,
                    category = category,
                    volunteerStartDate = volunteerStartDate,
                    volunteerEndDate = volunteerEndDate,
                    address = address,
                    totalCount = totalCount,
                    status = status,
                    recruitStartDate = recruitStartDate,
                    recruitEndDate = recruitEndDate,
                    imageUrl = imageUrl,
                    latitude = latitude,
                    longitude = longitude
                )

                Log.d("AdminActivity", "Creating Volunteer: $volunteer")

                createVolunteer(volunteer, dialog)
            } else {
                Toast.makeText(this, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun createVolunteer(volunteer: AdminVolunteerDTO, dialog: AlertDialog) {
        RetrofitClient.volunteerService.createVolunteer(volunteer)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminActivity, "봉사가 성공적으로 등록되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        refreshVolunteerList()
                    } else {
                        Toast.makeText(this@AdminActivity, "봉사 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(this@AdminActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun loadVolunteerDetail(volunteerId: Long) {
        RetrofitClient.volunteerService.getVolunteerDetail(volunteerId)
            .enqueue(object : Callback<VolunteerDetailResponse> {
                override fun onResponse(call: Call<VolunteerDetailResponse>, response: Response<VolunteerDetailResponse>) {
                    if (response.isSuccessful) {
                        response.body()?.data?.let { volunteerDetail ->
                            showEditVolunteerDialog(volunteerDetail)
                        }
                    } else {
                        Toast.makeText(this@AdminActivity, "봉사 상세 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VolunteerDetailResponse>, t: Throwable) {
                    Toast.makeText(this@AdminActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showEditVolunteerDialog(volunteer: VolunteerDetailDTO) {
        dialogView = layoutInflater.inflate(R.layout.dialog_add_volunteer, null)
        val builder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setTitle("봉사 수정")

        val dialog = builder.create()

        val editTextTitle = dialogView.findViewById<EditText>(R.id.editTextTitle)
        val editTextContent = dialogView.findViewById<EditText>(R.id.editTextContent)
        val editTextType = dialogView.findViewById<EditText>(R.id.editTextType)
        val editTextCategory = dialogView.findViewById<EditText>(R.id.editTextCategory)
        val editTextVolunteerStartDate = dialogView.findViewById<EditText>(R.id.editTextVolunteerStartDate)
        val editTextVolunteerEndDate = dialogView.findViewById<EditText>(R.id.editTextVolunteerEndDate)
        val editTextAddress = dialogView.findViewById<EditText>(R.id.editTextAddress)
        val editTextTotalCount = dialogView.findViewById<EditText>(R.id.editTextTotalCount)
        val editTextStatus = dialogView.findViewById<EditText>(R.id.editTextStatus)
        val editTextRecruitStartDate = dialogView.findViewById<EditText>(R.id.editTextRecruitStartDate)
        val editTextRecruitEndDate = dialogView.findViewById<EditText>(R.id.editTextRecruitEndDate)
        val editTextLatitude = dialogView.findViewById<EditText>(R.id.editTextLatitude)
        val editTextLongitude = dialogView.findViewById<EditText>(R.id.editTextLongitude)
        val buttonSelectImage = dialogView.findViewById<Button>(R.id.buttonSelectImage)
        val imageViewPreview = dialogView.findViewById<ImageView>(R.id.imageViewPreview)
        val buttonEditVolunteer = dialogView.findViewById<Button>(R.id.buttonAddVolunteer) // 동일 ID를 사용

        // 기존 데이터를 입력 필드에 설정
        editTextTitle.setText(volunteer.title)
        editTextContent.setText(volunteer.content)
        editTextType.setText(volunteer.type.toString())
        editTextCategory.setText(volunteer.category.toString())
        editTextVolunteerStartDate.setText(volunteer.volunteerStartDate?.substring(0, 10)) // 날짜 부분만 사용
        editTextVolunteerEndDate.setText(volunteer.volunteerEndDate?.substring(0, 10))
        editTextAddress.setText(volunteer.address)
        editTextTotalCount.setText(volunteer.totalCount.toString())
        editTextStatus.setText(volunteer.status.toString())
        editTextRecruitStartDate.setText(volunteer.recruitStartDate?.substring(0, 10))
        editTextRecruitEndDate.setText(volunteer.recruitEndDate?.substring(0, 10))
        editTextLatitude.setText(volunteer.latitude.toString())
        editTextLongitude.setText(volunteer.longitude.toString())

        buttonSelectImage.setOnClickListener {
            openImagePicker()
        }

        buttonEditVolunteer.setOnClickListener {
            val title = editTextTitle.text.toString()
            val content = editTextContent.text.toString()
            val type = editTextType.text.toString().toIntOrNull() ?: 0
            val category = editTextCategory.text.toString().toIntOrNull() ?: 0

            // 사용자가 입력한 날짜를 LocalDateTime으로 변환한 후 형식에 맞춰 문자열로 변환
            val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")

            val volunteerStartDate = editTextVolunteerStartDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }
            val volunteerEndDate = editTextVolunteerEndDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }
            val recruitStartDate = editTextRecruitStartDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }
            val recruitEndDate = editTextRecruitEndDate.text.toString().let {
                LocalDate.parse(it, dateFormatter).atStartOfDay().format(dateTimeFormatter)
            }

            val address = editTextAddress.text.toString()
            val totalCount = editTextTotalCount.text.toString().toIntOrNull() ?: 0
            val status = editTextStatus.text.toString().toIntOrNull() ?: 0

            val imageUrl =  selectedImageUri?.toString()
            val latitude = editTextLatitude.text.toString().toDoubleOrNull()
            val longitude = editTextLongitude.text.toString().toDoubleOrNull()

            if (title.isNotEmpty() && content.isNotEmpty()) {
                val updatedVolunteer = volunteer.copy(
                    title = title,
                    content = content,
                    type = type,
                    category = category,
                    volunteerStartDate = volunteerStartDate,
                    volunteerEndDate = volunteerEndDate,
                    address = address,
                    totalCount = totalCount,
                    status = status,
                    recruitStartDate = recruitStartDate,
                    recruitEndDate = recruitEndDate,
                    imageUrl = imageUrl,
                    latitude = latitude,
                    longitude = longitude
                )

                Log.d("AdminActivity", "Updating Volunteer: $updatedVolunteer")

                updateVolunteer(updatedVolunteer, dialog)
            } else {
                Toast.makeText(this, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateVolunteer(volunteer: VolunteerDetailDTO, dialog: AlertDialog) {
        RetrofitClient.volunteerService.updateVolunteer(volunteer.volunteerId, volunteer)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminActivity, "봉사가 성공적으로 수정되었습니다.", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        refreshVolunteerList()
                    } else {
                        Toast.makeText(this@AdminActivity, "봉사 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(this@AdminActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun showDeleteConfirmationDialog(volunteerId: Long) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("봉사 삭제")
        builder.setMessage("정말로 이 봉사를 삭제하시겠습니까?")
        builder.setPositiveButton("예") { dialog, _ ->
            deleteVolunteer(volunteerId)
            dialog.dismiss()
        }
        builder.setNegativeButton("아니오") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    private fun deleteVolunteer(volunteerId: Long) {
        RetrofitClient.volunteerService.deleteVolunteer(volunteerId)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(call: Call<ApiResponse<Void>>, response: Response<ApiResponse<Void>>) {
                    if (response.isSuccessful) {
                        Toast.makeText(this@AdminActivity, "봉사가 성공적으로 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                        refreshVolunteerList()
                    } else {
                        Toast.makeText(this@AdminActivity, "봉사 삭제에 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(this@AdminActivity, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_PICK)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            dialogView.findViewById<ImageView>(R.id.imageViewPreview).setImageURI(selectedImageUri)
        }
    }

    companion object {
        const val REQUEST_IMAGE_PICK = 1
    }
}
