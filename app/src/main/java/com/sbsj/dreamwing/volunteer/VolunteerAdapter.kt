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
            Log.d("VolunteerAdapter", "Binding volunteer with ID: ${volunteer.volunteerId}")

            with(binding) {
                titleTextView.text = volunteer.title
                categoryTextView.text = volunteer.getCategoryName()
                addressTextView.text = volunteer.address
                volunteerStartDateTextView.text = volunteer.getFormattedStartDate()
                statusTextView.text = if (volunteer.status == 1) "모집중" else "모집완료"
                setImage(volunteer.imageUrl)
                root.setOnClickListener { onItemClick(volunteer) }
            }
        }

        private fun ItemVolunteerBinding.setImage(imageUrl: String) {
            if (imageUrl.isNotEmpty()) {
                Picasso.get().load(imageUrl).into(imageView)
                imageView.visibility = View.VISIBLE
            } else {
                imageView.visibility = View.GONE
            }
        }

        private fun VolunteerListDTO.getCategoryName() = when (category) {
            1 -> "빵만들기"
            2 -> "자막달기"
            3 -> "돌보기"
            4 -> "밑반찬 만들기"
            5 -> "흙공 만들기"
            else -> "기타"
        }

        private fun VolunteerListDTO.getFormattedStartDate(): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault())
            val date = inputFormat.parse(volunteerStartDate)
            return outputFormat.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val binding = ItemVolunteerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolunteerViewHolder(binding)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        holder.bind(volunteers[position])
    }

    override fun getItemCount(): Int = volunteers.size
}
