package com.sbsj.dreamwing.volunteer

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.data.api.ApiService
import com.sbsj.dreamwing.data.model.VolunteerListDTO
import com.sbsj.dreamwing.data.model.VolunteerListResponse
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class VolunteerActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var volunteerAdapter: VolunteerAdapter
    private var volunteerList: MutableList<VolunteerListDTO> = mutableListOf()
    private var page = 0
    private val size = 2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_volunteer)

        recyclerView = findViewById(R.id.recyclerView)
        volunteerAdapter = VolunteerAdapter(volunteerList)
        recyclerView.adapter = volunteerAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    loadMoreVolunteers()
                }
            }
        })

        loadMoreVolunteers()
    }

    private fun loadMoreVolunteers() {
        val apiService = Retrofit.Builder()
            .baseUrl("http:/10.0.2.2")  // Ensure this URL is correct
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)

        apiService.getVolunteerList(page, size).enqueue(object : Callback<VolunteerListResponse> {
            override fun onResponse(
                call: Call<VolunteerListResponse>,
                response: Response<VolunteerListResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let { volunteerListResponse ->
                        Log.d("VolunteerActivity", "Data received: ${volunteerListResponse.data.size} items")
                        volunteerList.addAll(volunteerListResponse.data)
                        volunteerAdapter.notifyDataSetChanged()
                        page++
                    }
                } else {
                    Log.e("VolunteerActivity", "Response failed with code: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<VolunteerListResponse>, t: Throwable) {
                Log.e("VolunteerActivity", "API call failed", t)
            }
        })
    }
}
