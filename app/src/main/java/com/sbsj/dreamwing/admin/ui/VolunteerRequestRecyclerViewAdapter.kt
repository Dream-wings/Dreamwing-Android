package com.sbsj.dreamwing.admin.ui

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.admin.model.response.VolunteerRequestListResponse
import com.sbsj.dreamwing.databinding.ItemAdminBinding
import com.sbsj.dreamwing.volunteer.model.VolunteerType

/**
 * 봉사활동 신청 대기 목록 View Adapter
 * @author 정은지
 * @since 2024.08.04
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04  	정은지       최초 생성
 * </pre>
 */
class VolunteerRequestRecyclerViewAdapter(
    private val requestList: MutableList<VolunteerRequestListResponse>,
    private val context: Context
) : RecyclerView.Adapter<VolunteerRequestRecyclerViewAdapter.RequestListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestListViewHolder {
        val binding = ItemAdminBinding.inflate(LayoutInflater.from(context), parent, false)
        return RequestListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RequestListViewHolder, position: Int) {
        val item = requestList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = requestList.size

    inner class RequestListViewHolder(private val binding: ItemAdminBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION && position < requestList.size) {
                    val item = requestList[position]
                    val intent = Intent(context, VolunteerRequestDetailActivity::class.java).apply {
                        putExtra("volunteerId", item.volunteerId)
                        putExtra("userId", item.userId)
                    }
                    context.startActivity(intent)
                }
            }
        }

        fun bind(item: VolunteerRequestListResponse) {
            Log.d("VolunteerRequestRecyclerViewAdapter", item.toString())
            binding.column1.text = item.volunteerId.toString()
            val type = VolunteerType.fromCode(item.type)
            binding.column2.text = type
            binding.column3.text = item.title
            binding.column4.text = item.loginId
        }
    }
}