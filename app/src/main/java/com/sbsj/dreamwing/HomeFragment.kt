package com.sbsj.dreamwing

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentHomeBinding
import com.sbsj.dreamwing.support.model.response.SupportListResponse
import com.sbsj.dreamwing.support.model.response.TotalSupportResponse
import com.sbsj.dreamwing.support.ui.SupportRecyclerViewAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 홈 프래그먼트
 * @author 정은지
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	정은지       최초 생성
 * </pre>
 */
class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var supportRecyclerViewAdapter: SupportRecyclerViewAdapter
    private lateinit var supportList: ArrayList<SupportListResponse>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun fetchSupportList() {
        RetrofitClient.supportService.getSupportList().enqueue(object : Callback<ApiResponse<List<SupportListResponse>>> {
            override fun onResponse(
                call: Call<ApiResponse<List<SupportListResponse>>>,
                response: Response<ApiResponse<List<SupportListResponse>>>
            ) {
                if (response.isSuccessful) {
                    val supportData = response.body()?.data
                    Log.d("HomeFragment", "SupportList: $supportData")
                    supportData?.let {
                        supportList.addAll(it)
                        supportRecyclerViewAdapter.notifyDataSetChanged()
                    }
                } else {
                    Log.e("HomeFragment", "Response not successful")
                }
            }

            override fun onFailure(call: Call<ApiResponse<List<SupportListResponse>>>, t: Throwable) {
                Log.e("HomeFragment", "Error: ${t.message}")
            }
        })
    }

    private fun fetchTotalSupport() {
        RetrofitClient.supportService.getTotalSupport().enqueue(object : Callback<ApiResponse<TotalSupportResponse>> {
            override fun onResponse(
                call: Call<ApiResponse<TotalSupportResponse>>,
                response: Response<ApiResponse<TotalSupportResponse>>
            ) {
                if (response.isSuccessful) {
                    val support = response.body()?.data
                    Log.d("HomeFragment", "TotalSupport: $support")
                    support?.let {
                        binding.supportPoint.text = it.totalPoints.toString() + " 원"
                        binding.supportCount.text = it.totalCount.toString() + " 건"
                    }
                } else {
                    Log.e("HomeFragment", "Response not successful")
                    binding.supportPoint.text= "0"
                }
            }

            override fun onFailure(call: Call<ApiResponse<TotalSupportResponse>>, t: Throwable) {
                Log.e("HomeFragment", "Error: ${t.message}")
                binding.supportPoint.text = "0"
            }
        })
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        supportList = ArrayList()
        supportRecyclerViewAdapter = SupportRecyclerViewAdapter(supportList, requireContext())
        binding.recyclerView.adapter = supportRecyclerViewAdapter

        fetchSupportList()
        fetchTotalSupport()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}