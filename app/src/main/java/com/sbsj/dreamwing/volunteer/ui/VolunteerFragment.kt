package com.sbsj.dreamwing.volunteer.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.common.model.ApiResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentVolunteerBinding
import com.sbsj.dreamwing.volunteer.VolunteerAdapter
import com.sbsj.dreamwing.volunteer.VolunteerDetailActivity
import com.sbsj.dreamwing.volunteer.model.PostApplyVolunteerRequestDTO
import com.sbsj.dreamwing.volunteer.model.VolunteerListDTO
import com.sbsj.dreamwing.volunteer.model.response.VolunteerListResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class VolunteerFragment : Fragment() {

    private var _binding: FragmentVolunteerBinding? = null
    private val binding get() = _binding!!

    private lateinit var volunteerAdapter: VolunteerAdapter
    private var volunteerList: MutableList<VolunteerListDTO> = mutableListOf()
    private var page = 0
    private val size = 2
    private var userId: Long = 2L // Replace with the actual user ID

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVolunteerBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize VolunteerAdapter
        volunteerAdapter = VolunteerAdapter(volunteerList) { volunteer ->
            Log.d("VolunteerFragment", "Checking apply status for volunteerId: ${volunteer.volunteerId}")

            checkIfUserApplied(volunteer.volunteerId) { hasApplied ->
                val intent = Intent(requireContext(), VolunteerDetailActivity::class.java).apply {
                    putExtra("volunteerId", volunteer.volunteerId)
                    putExtra("userId", userId) // Pass the userId to detail page
                    putExtra("hasApplied", hasApplied) // Pass the apply status
                }
                startActivity(intent)
            }
        }

        // Set the adapter and layout manager for RecyclerView
        binding.recyclerView.adapter = volunteerAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load volunteers
        loadMoreVolunteers()

        // Set scroll listener to load more volunteers on scroll
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreVolunteers()
                }
            }
        })

        return view
    }

    private fun loadMoreVolunteers() {
        RetrofitClient.volunteerService.getVolunteerList(page, size).enqueue(object : Callback<VolunteerListResponse> {
            override fun onResponse(
                call: Call<VolunteerListResponse>,
                response: Response<VolunteerListResponse>
            ) {
                if (response.isSuccessful) {
                    Log.d("VolunteerFragment", "Volunteer list : $response")

                    response.body()?.let { volunteerListResponse ->
                        volunteerList.addAll(volunteerListResponse.data)
                        volunteerAdapter.notifyDataSetChanged()
                        page++
                    }
                }
            }

            override fun onFailure(call: Call<VolunteerListResponse>, t: Throwable) {
                // Handle API failure
                Log.e("VolunteerFragment", "Failed to load volunteers", t)
            }
        })
    }

    private fun checkIfUserApplied(volunteerId: Long, callback: (Boolean) -> Unit) {
        RetrofitClient.volunteerService.checkIfAlreadyApplied(
            PostApplyVolunteerRequestDTO(volunteerId, userId)
        ).enqueue(object : Callback<ApiResponse<Boolean>> {
            override fun onResponse(
                call: Call<ApiResponse<Boolean>>,
                response: Response<ApiResponse<Boolean>>
            ) {
                if (response.isSuccessful) {
                    val apiResponse = response.body()
                    if (apiResponse != null && apiResponse.success) {
                        callback(apiResponse.data ?: false)
                    } else {
                        callback(false)
                        Log.e("VolunteerFragment", "API response error: ${apiResponse?.message}")
                    }
                } else {
                    callback(false)
                    Log.e("VolunteerFragment", "Response error: ${response.errorBody()?.string()}")
                }
            }

            override fun onFailure(call: Call<ApiResponse<Boolean>>, t: Throwable) {
                callback(false)
                Log.e("VolunteerFragment", "Network request failed", t)
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = VolunteerFragment()
    }
}
