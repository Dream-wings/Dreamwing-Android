package com.sbsj.dreamwing.admin

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.admin.adapter.AdminItemAdapter
import com.sbsj.dreamwing.admin.model.response.AdminVolunteerDTO
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListDTO
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentAdminBinding
import com.sbsj.dreamwing.volunteer.model.VolunteerDetailDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerDetailResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var volunteerAdapter: AdminItemAdapter
    private val volunteerList: MutableList<VolunteerAdminListDTO> = mutableListOf()
    private var volunteerPage = 0
    private val pageSize = 10

    private var selectedVolunteerId: Long? = null // 선택된 봉사 ID
    private var selectedImageUri: Uri? = null
    private lateinit var dialogView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
                Toast.makeText(requireContext(), "수정할 항목을 선택해 주세요.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupRecyclerView() {
        volunteerAdapter = AdminItemAdapter(volunteerList) { volunteerId ->
            selectedVolunteerId = volunteerId
            Toast.makeText(requireContext(), "선택된 ID: $volunteerId", Toast.LENGTH_SHORT).show()
        }
        binding.volunteerRecyclerView.adapter = volunteerAdapter
        binding.volunteerRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.volunteerRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
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
                        Toast.makeText(requireContext(), "봉사 목록을 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VolunteerAdminListResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun refreshVolunteerList() {
        volunteerPage = 0
        volunteerList.clear()
        loadMoreVolunteers()
    }

    private fun showAddVolunteerDialog() {
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_volunteer, null)
        val builder = AlertDialog.Builder(context)
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

                Log.d("AdminFragment", "Creating Volunteer: $volunteer")

                createVolunteer(volunteer, dialog)
            } else {
                Toast.makeText(context, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun createVolunteer(volunteer: AdminVolunteerDTO, dialog: AlertDialog) {
        RetrofitClient.volunteerService.createVolunteer(volunteer)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(
                    call: Call<ApiResponse<Void>>,
                    response: Response<ApiResponse<Void>>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "봉사가 등록되었습니다", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        refreshVolunteerList()
                    } else {
                        Toast.makeText(requireContext(), "봉사 등록에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("AdminFragment", "Response Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AdminFragment", "Network Error: ${t.message}")
                }
            })
    }

    // AdminFragment.kt
    private fun loadVolunteerDetail(volunteerId: Long) {
        // 선택된 봉사 ID에 대한 상세 정보 로드
        RetrofitClient.volunteerService.getVolunteerDetail(volunteerId)
            .enqueue(object : Callback<VolunteerDetailResponse> { // Corrected callback type
                override fun onResponse(
                    call: Call<VolunteerDetailResponse>,
                    response: Response<VolunteerDetailResponse>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        response.body()?.data?.let { volunteer ->
                            showEditVolunteerDialog(volunteer)
                        }
                    } else {
                        Toast.makeText(requireContext(), "봉사 상세 정보를 가져오는데 실패했습니다.", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<VolunteerDetailResponse>, t: Throwable) { // Corrected failure callback type
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun showEditVolunteerDialog(volunteer: VolunteerDetailDTO) {
        dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_volunteer, null)
        val builder = AlertDialog.Builder(context)
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
        val buttonEditVolunteer = dialogView.findViewById<Button>(R.id.buttonAddVolunteer)

        // 기존 데이터 설정
        editTextTitle.setText(volunteer.title)
        editTextContent.setText(volunteer.content)
        editTextType.setText(volunteer.type.toString())
        editTextCategory.setText(volunteer.category.toString())
        editTextVolunteerStartDate.setText(volunteer.volunteerStartDate?.substring(0, 10))
        editTextVolunteerEndDate.setText(volunteer.volunteerEndDate?.substring(0, 10))
        editTextAddress.setText(volunteer.address)
        editTextTotalCount.setText(volunteer.totalCount.toString())
        editTextStatus.setText(volunteer.status.toString())
        editTextRecruitStartDate.setText(volunteer.recruitStartDate?.substring(0, 10))
        editTextRecruitEndDate.setText(volunteer.recruitEndDate?.substring(0, 10))
        editTextLatitude.setText(volunteer.latitude.toString())
        editTextLongitude.setText(volunteer.longitude.toString())
        selectedImageUri = volunteer.imageUrl?.let { Uri.parse(it) }

        // 이미지 미리보기 설정
        selectedImageUri?.let {
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, it)
                imageViewPreview.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        buttonSelectImage.setOnClickListener {
            openImagePicker()
        }

        buttonEditVolunteer.text = "수정 완료"
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

            val imageUrl = selectedImageUri?.toString()
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

                updateVolunteer(updatedVolunteer, dialog)
            } else {
                Toast.makeText(context, "모든 필드를 입력하세요", Toast.LENGTH_SHORT).show()
            }
        }

        dialog.show()
    }

    private fun updateVolunteer(volunteer: VolunteerDetailDTO, dialog: AlertDialog) {
        RetrofitClient.volunteerService.updateVolunteer(volunteer.volunteerId, volunteer)
            .enqueue(object : Callback<ApiResponse<Void>> {
                override fun onResponse(
                    call: Call<ApiResponse<Void>>,
                    response: Response<ApiResponse<Void>>
                ) {
                    if (response.isSuccessful && response.body()?.success == true) {
                        Toast.makeText(requireContext(), "봉사가 수정되었습니다", Toast.LENGTH_SHORT).show()
                        dialog.dismiss()
                        refreshVolunteerList()
                    } else {
                        Toast.makeText(requireContext(), "봉사 수정에 실패했습니다.", Toast.LENGTH_SHORT).show()
                        Log.e("AdminFragment", "Response Error: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<ApiResponse<Void>>, t: Throwable) {
                    Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                    Log.e("AdminFragment", "Network Error: ${t.message}")
                }
            })
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, IMAGE_PICKER_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == IMAGE_PICKER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data

            try {
                val bitmap = MediaStore.Images.Media.getBitmap(activity?.contentResolver, selectedImageUri)
                dialogView.findViewById<ImageView>(R.id.imageViewPreview).setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val IMAGE_PICKER_REQUEST_CODE = 1
    }
}
