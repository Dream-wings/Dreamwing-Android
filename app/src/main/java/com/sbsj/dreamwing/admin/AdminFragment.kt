package com.sbsj.dreamwing.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.admin.adapter.AdminItemAdapter
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListDTO
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentAdminBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!

    private lateinit var volunteerAdapter: AdminItemAdapter
    private val volunteerList: MutableList<VolunteerAdminListDTO> = mutableListOf()
    private var volunteerPage = 0
    private val pageSize = 10

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
    }

    private fun setupRecyclerView() {
        volunteerAdapter = AdminItemAdapter(volunteerList)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
