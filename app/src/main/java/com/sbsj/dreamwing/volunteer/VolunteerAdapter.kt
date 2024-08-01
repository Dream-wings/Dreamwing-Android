package com.sbsj.dreamwing.volunteer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.databinding.ItemVolunteerBinding
import com.sbsj.dreamwing.volunteer.model.VolunteerListDTO
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

class VolunteerAdapter(
    private val volunteers: List<VolunteerListDTO>,
    private val onItemClick: (VolunteerListDTO) -> Unit
) : RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    inner class VolunteerViewHolder(private val binding: ItemVolunteerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(volunteer: VolunteerListDTO) {
            Log.d("VolunteerAdapter", "Binding volunteer with ID: ${volunteer.volunteerId}") // 추가된 로그

            binding.titleTextView.text = volunteer.title
            binding.categoryTextView.text = when (volunteer.category) {
                1 -> "빵만들기"
                2 -> "청각장애인을 위한 자막달기"
                3 -> "신생아 돌보기"
                4 -> "밑반찬 만들기"
                5 -> "환경정화를 위한 흙공 만들기"
                else -> "기타"
            }
            binding.addressTextView.text = volunteer.address
            binding.volunteerStartDateTextView.text = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault()).format(SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault()).parse(volunteer.volunteerStartDate))
            binding.statusTextView.text = if (volunteer.status == 1) "모집중" else "모집완료"

            if (volunteer.imageUrl.isNotEmpty()) {
                Picasso.get().load(volunteer.imageUrl).into(binding.imageView)
                binding.imageView.visibility = View.VISIBLE
            } else {
                binding.imageView.visibility = View.GONE
            }

            binding.root.setOnClickListener {
                Log.d("VolunteerAdapter", "Item clicked with volunteerId: ${volunteer.volunteerId}")

                onItemClick(volunteer)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val binding = ItemVolunteerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolunteerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        holder.bind(volunteers[position])
    }

    override fun getItemCount(): Int {
        return volunteers.size
    }
}
