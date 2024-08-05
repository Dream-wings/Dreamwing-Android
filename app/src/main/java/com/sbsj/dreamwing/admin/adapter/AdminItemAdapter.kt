package com.sbsj.dreamwing.admin.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.admin.model.response.VolunteerAdminListDTO

class AdminItemAdapter(
    private val itemList: List<VolunteerAdminListDTO>,
) : RecyclerView.Adapter<AdminItemAdapter.AdminItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin2, parent, false)
        return AdminItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class AdminItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val numberTextView: TextView = itemView.findViewById(R.id.numberTextView)
        private val typeTextView: TextView = itemView.findViewById(R.id.typeTextView)
        private val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        private val countTextView: TextView = itemView.findViewById(R.id.countTextView)

        fun bind(item: VolunteerAdminListDTO) {
            numberTextView.text = item.volunteerId.toString()
            typeTextView.text = when (item.type) {
                0 -> "봉사"
                1 -> "멘토링"
                else -> "기타"
            }
            titleTextView.text = item.title ?: "제목 없음"
            countTextView.text = "${item.currentCount}/${item.totalCount}"
        }
    }
}
