package com.sbsj.dreamwing.support.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.databinding.FragmentSupportBinding
import com.sbsj.dreamwing.support.model.response.GetSupportListResponse
import com.sbsj.dreamwing.support.model.response.SupportListResponse
import com.sbsj.dreamwing.data.api.RetrofitClient
import com.sbsj.dreamwing.support.ui.SupportRecyclerViewAdapter
import android.graphics.Color
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    private lateinit var supportAdapter: SupportRecyclerViewAdapter
    private var supportList: MutableList<SupportListResponse> = mutableListOf()
    private var page = 0
    private val size = 2 // Number of items to load per page

    // Default selections
    private var selectedStatus = 0 // 0 for 모금중, 1 for 모금완료
    private var selectedCategory = 1 // Default to 1 for 교육

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)
        val view = binding.root

        // Initialize SupportAdapter
        supportAdapter = SupportRecyclerViewAdapter(supportList, requireContext())

        // Set the adapter and layout manager for RecyclerView
        binding.recyclerView.adapter = supportAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

        // Load initial support items
        loadMoreSupportItems()

        // Set scroll listener to load more support items on scroll
        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) { // Check if scrolled to the bottom
                    loadMoreSupportItems()
                }
            }
        })

        // Initialize buttons with default selections
        setupStatusButtons()
        setupCategoryButtons()

        return view
    }

    private fun setupStatusButtons() {
        val options = listOf(binding.optionInFundraising, binding.optionCompleted)

        options.forEach { option ->
            option.setOnClickListener { view ->
                val selected = (view as TextView)
                options.forEach {
                    it.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_recruit_end, 0, 0, 0)
                    it.setTextColor(Color.parseColor("#767676"))
                }
                selected.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_recruit_started, 0, 0, 0)
                selected.setTextColor(Color.BLACK)

                selectedStatus = if (selected.id == R.id.optionInFundraising) 0 else 1
                refreshSupportItems()
            }
        }

        // Set the initial state for the first option (모금중)
        binding.optionInFundraising.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_recruit_started, 0, 0, 0)
        binding.optionInFundraising.setTextColor(Color.BLACK)
    }

    private fun setupCategoryButtons() {
        val buttons = listOf(binding.btnEducation, binding.btnSupplies, binding.btnArts, binding.btnHousing)

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                selectedCategory = index + 1 // Category ID corresponds to index + 1
                refreshSupportItems()
                updateButtonStates(button, buttons)
            }
        }

        // Set the initial state (교육)
        updateButtonStates(binding.btnEducation, buttons)
    }

    private fun updateButtonStates(selectedButton: Button, allButtons: List<Button>) {
        allButtons.forEach { button ->
            if (button == selectedButton) {
                button.setBackgroundResource(R.drawable.button_jaeseong)
                button.setTextColor(Color.WHITE)
            } else {
                button.setBackgroundResource(R.drawable.button_jaeseong_selector)
                button.setTextColor(Color.parseColor("#767676"))
            }
        }
    }

    private fun loadMoreSupportItems() {
        RetrofitClient.supportService.getSupportList(page, size, selectedStatus, selectedCategory)
            .enqueue(object : Callback<GetSupportListResponse> {
                override fun onResponse(
                    call: Call<GetSupportListResponse>,
                    response: Response<GetSupportListResponse>
                ) {
                    if (response.isSuccessful) {
                        Log.d("SupportFragment", "Support list: ${response.body()}")

                        response.body()?.let { supportListResponse ->
                            if (supportListResponse.success) { // Check success flag
                                // Avoid duplicate items
                                val newData = supportListResponse.data
                                val newSupportList = supportList.toMutableList()

                                // Check if any item is already in the list
                                newData.forEach { newItem ->
                                    if (newSupportList.none { it.supportId == newItem.supportId }) {
                                        newSupportList.add(newItem)
                                    }
                                }

                                // Update the supportList with new data
                                supportList.clear()
                                supportList.addAll(newSupportList)

                                supportAdapter.notifyDataSetChanged() // Notify the adapter
                                page++ // Increment the page count for pagination
                            } else {
                                Log.e("SupportFragment", "Failed: ${supportListResponse.message}")
                            }
                        }
                    } else {
                        Log.e("SupportFragment", "Failed to load support items: ${response.errorBody()?.string()}")
                    }
                }

                override fun onFailure(call: Call<GetSupportListResponse>, t: Throwable) {
                    Log.e("SupportFragment", "Failed to load support items", t)
                }
            })
    }

    private fun refreshSupportItems() {
        page = 0
        supportList.clear()
        supportAdapter.notifyDataSetChanged()
        loadMoreSupportItems()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        @JvmStatic
        fun newInstance() = SupportFragment()
    }
}
