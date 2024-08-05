package com.sbsj.dreamwing.admin.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestListResponse
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentVolunteerRequestListBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 봉사활동 인증 대기 목록 화면
 * @author 정은지
 * @since 2024.08.05
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.05  	정은지       최초 생성
 * </pre>
 */
class VolunteerCertificationListFragment : Fragment() {

    private var _binding: FragmentVolunteerRequestListBinding? = null
    private val binding get() = _binding!!

    private lateinit var volunteerCertificationRecyclerViewAdapter: VolunteerCertificationRecyclerViewAdapter
    private val requestList = mutableListOf<VolunteerRequestListResponse>()
    private var page = 0
    private val size = 10

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVolunteerRequestListBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // RecyclerView 설정
        volunteerCertificationRecyclerViewAdapter = VolunteerCertificationRecyclerViewAdapter(requestList, requireContext())
        binding.recyclerView.adapter = volunteerCertificationRecyclerViewAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // 데이터 불러오기
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    fetchRequestList()
                }
            }
        })

        // 처음 데이터 불러오기
        fetchRequestList()
    }

    private fun fetchRequestList() {
        RetrofitClient.adminService.getVolunteerCertificationList(page, size).enqueue(object :
            Callback<ApiResponse<List<VolunteerRequestListResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<VolunteerRequestListResponse>>>,
                response: Response<ApiResponse<List<VolunteerRequestListResponse>>>
            ) {
                if (response.isSuccessful) {
                    val requestData = response.body()?.data
                    Log.d("VolunteerCertificationListFragment", "RequestList: $requestData")
                    requestData?.let {
                        requestList.addAll(it)
                        volunteerCertificationRecyclerViewAdapter.notifyDataSetChanged()
                        page++
                    }
                } else {
                    Log.e("VolunteerCertificationListFragment", "Response not successful")
                }
            }

            override fun onFailure(
                call: Call<ApiResponse<List<VolunteerRequestListResponse>>>,
                t: Throwable
            ) {
                Log.e("VolunteerCertificationListFragment", "Error: ${t.message}")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}