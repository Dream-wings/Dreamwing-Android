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
    private val onItemClickListener: (volunteerId: Long) -> Unit // 클릭 리스너 추가
) : RecyclerView.Adapter<AdminItemAdapter.AdminItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminItemViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_admin, parent, false)
        return AdminItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminItemViewHolder, position: Int) {
        val item = itemList[position]
        holder.bind(item)
        holder.itemView.setOnClickListener {
            onItemClickListener(item.volunteerId) // 클릭 시 리스너 호출
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    inner class AdminItemViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val column1TextView: TextView = itemView.findViewById(R.id.column1)
        private val column2TextView: TextView = itemView.findViewById(R.id.column2)
        private val column3TextView: TextView = itemView.findViewById(R.id.column3)
        private val column4TextView: TextView = itemView.findViewById(R.id.column4)

        fun bind(item: VolunteerAdminListDTO) {
            // Assuming volunteerId maps to column1
            column1TextView.text = item.volunteerId.toString()

            // Assuming type maps to column2
            column2TextView.text = when (item.type) {
                0 -> "봉사"
                1 -> "멘토링"
                else -> "기타"
            }

            // Assuming title maps to column3
            column3TextView.text = item.title ?: "제목 없음"

            // Assuming currentCount and totalCount maps to column4
            column4TextView.text = "${item.currentCount}/${item.totalCount}"
        }
    }
}
