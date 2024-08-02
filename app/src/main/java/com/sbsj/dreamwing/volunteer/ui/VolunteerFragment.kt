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
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.databinding.FragmentVolunteerBinding
import com.sbsj.dreamwing.volunteer.VolunteerAdapter
import com.sbsj.dreamwing.volunteer.VolunteerDetailActivity
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentVolunteerBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize VolunteerAdapter
        volunteerAdapter = VolunteerAdapter(volunteerList) { volunteer ->
            Log.d("VolunteerFragment", "Navigating to details with volunteerId: ${volunteer.volunteerId}")

            val intent = Intent(requireContext(), VolunteerDetailActivity::class.java)
            intent.putExtra("volunteerId", volunteer.volunteerId.toLong())
            startActivity(intent)
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
                    Log.d("VolunteerDetailFragment", "Volunteer list : $response")

                    response.body()?.let { volunteerListResponse ->
                        volunteerList.addAll(volunteerListResponse.data)
                        volunteerAdapter.notifyDataSetChanged()
                        page++
                    }
                }
            }

            override fun onFailure(call: Call<VolunteerListResponse>, t: Throwable) {
                // Handle API failure
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
