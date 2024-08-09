package com.sbsj.dreamwing.support.ui

import android.content.Context
import android.content.Intent
import android.icu.text.DecimalFormat
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.databinding.ItemSupportBinding
import com.sbsj.dreamwing.support.SupportDetailActivity
import com.sbsj.dreamwing.support.model.response.SupportListResponse
import com.squareup.picasso.Picasso

/**
 * 후원 목록 View Adapter
 * @author 정은지
 * @since 2024.08.01
 * @version 1.0
 *
 * <pre>
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.01  	정은지       최초 생성
 * </pre>
 */
class SupportRecyclerViewAdapter(
    private val supportList: List<SupportListResponse>,
    private val context: Context
) : RecyclerView.Adapter<SupportRecyclerViewAdapter.SupportListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportListViewHolder {
        val binding = ItemSupportBinding.inflate(LayoutInflater.from(context), parent, false)
        return SupportListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupportListViewHolder, position: Int) {
        val item = supportList[position]
        holder.bind(item)

        holder.itemView.setOnClickListener {
            val intent = Intent(context, SupportDetailActivity::class.java)
            intent.putExtra("supportId", item.supportId)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return supportList.size
    }

    class SupportListViewHolder(private val binding: ItemSupportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SupportListResponse) {
            Log.d("SupportRecyclerViewAdapter", item.toString())
            binding.title.text = item.title

            val decimalFormat = DecimalFormat("#,###")
            val formattedGoal: String = decimalFormat.format(item.goalPoint)
            binding.amount.text = formattedGoal

            val progressPercentage = calculateProgressPercentage(item.goalPoint, item.currentPoint)
            binding.progressPercentage.text = "$progressPercentage%"
            binding.progressBar.progress = progressPercentage

            val timeRemaining = calculateTimeRemaining(item.dday)
            binding.timeRemaining.text = timeRemaining

            if (item.imageUrl.isNotEmpty()) {
                Picasso.get().load(item.imageUrl).into(binding.image)
                binding.image.visibility = View.VISIBLE
            } else {
                binding.image.visibility = View.GONE
            }
        }

        /**
         *  남은 시간 계산 반환 메서드
         */
        private fun calculateTimeRemaining(dDay: String): String {
            val trimmedDay = dDay.trimStart('-')
            val parts = trimmedDay.split(" ")
            val days = parts[0]
            val timeParts = parts[1].split(":")
            val hours = timeParts[0]

            return days+"일 "+hours+"시간"
        }

        /**
         * 퍼센트 계산 메서드
         */
        private fun calculateProgressPercentage(goalPoint: Int, currentPoint: Int): Int {
            return if (goalPoint == 0) {
                0
            } else {
                (currentPoint * 100) / goalPoint
            }
        }
    }
}
