package com.sbsj.dreamwing.volunteer

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.data.model.VolunteerListDTO
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.*

class VolunteerAdapter(private val volunteers: List<VolunteerListDTO>) :
    RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {

    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.titleTextView)
//        val type: TextView = itemView.findViewById(R.id.typeTextView)
        val category: TextView = itemView.findViewById(R.id.categoryTextView)
        val volunteerStartDate: TextView = itemView.findViewById(R.id.volunteerStartDateTextView)
//        val volunteerEndDate: TextView = itemView.findViewById(R.id.volunteerEndDateTextView)
        val address: TextView = itemView.findViewById(R.id.addressTextView)
        val status: TextView = itemView.findViewById(R.id.statusTextView)
        val image: ImageView = itemView.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_volunteer, parent, false)
        return VolunteerViewHolder(view)
    }

    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        val volunteer = volunteers[position]

        // Load the image
        if (volunteer.imageUrl.isNotEmpty()) {
            Picasso.get().load(volunteer.imageUrl).into(holder.image)
            holder.image.visibility = View.VISIBLE
        } else {
            holder.image.visibility = View.GONE
        }

        // Set the category and title in one line
        holder.category.text = when (volunteer.category) {
            1 -> "빵만들기"
            2 -> "청각장애인을 위한 자막달기"
            3 -> "신생아 돌보기"
            4 -> "밑반찬 만들기"
            5 -> "환경정화를 위한 흙공 만들기"
            else -> "기타"
        }
        holder.title.text = volunteer.title

        // Set the address
        holder.address.text = volunteer.address
        holder.address.visibility = if (volunteer.address.isNotEmpty()) View.VISIBLE else View.GONE

        // Format the date
        val startDate = volunteer.volunteerStartDate
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
        val date = sdf.parse(startDate)
        val formattedDate = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.getDefault()).format(date)

        // Set the volunteer start date and status
        holder.volunteerStartDate.text = formattedDate
        holder.volunteerStartDate.visibility = if (startDate.isNotEmpty()) View.VISIBLE else View.GONE

        holder.status.text = if (volunteer.status == 1) "모집중" else "모집종료"
        holder.status.visibility = if (volunteer.status != -1) View.VISIBLE else View.GONE
    }

    override fun getItemCount(): Int {
        return volunteers.size
    }
}
