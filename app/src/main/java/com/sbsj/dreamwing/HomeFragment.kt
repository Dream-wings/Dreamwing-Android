package com.sbsj.dreamwing

import android.content.Intent
import android.icu.text.DecimalFormat
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
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
import androidx.navigation.fragment.findNavController
import com.sbsj.dreamwing.mission.ui.QuizActivity
import com.sbsj.dreamwing.mission.ui.WalkActivity
import com.sbsj.dreamwing.volunteer.ui.VolunteerFragment
import java.text.SimpleDateFormat
import java.util.Locale

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
                        val decimalFormat = DecimalFormat("#,###")
                        binding.supportPoint.text = decimalFormat.format(it.totalPoints) + " 원"
                        binding.supportCount.text = decimalFormat.format(it.totalCount) + " 건"
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

        binding.walkIcon.setOnClickListener {
            val intent = Intent(requireContext(), WalkActivity::class.java)
            startActivity(intent)
        }

        binding.quizIcon.setOnClickListener {
            val intent = Intent(requireContext(), QuizActivity::class.java)
            startActivity(intent)
        }

        binding.volunteerIcon.setOnClickListener {
            val volunteerFragment = VolunteerFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.action_homeFragment_to_volunteerFragment,
                    volunteerFragment)
                .commit()
        }

        binding.viewAll.setOnClickListener {
            val volunteerFragment = VolunteerFragment()
            requireActivity().supportFragmentManager
                .beginTransaction()
                .replace(
                    R.id.action_homeFragment_to_supportFragment,
                    volunteerFragment)
                .commit()
        }

        supportList = ArrayList()
        supportRecyclerViewAdapter = SupportRecyclerViewAdapter(supportList, requireContext())
        binding.recyclerView.adapter = supportRecyclerViewAdapter

        val dateFormat = SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault())
        val currentDateTime = dateFormat.format(System.currentTimeMillis())
        val currentDate = "$currentDateTime 기준"

        binding.currentDate.text = currentDate

        binding.currentDate
        fetchSupportList()
        fetchTotalSupport()



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}