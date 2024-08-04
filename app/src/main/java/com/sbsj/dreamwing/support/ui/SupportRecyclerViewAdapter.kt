package com.sbsj.dreamwing.support.ui

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.databinding.ItemSupportBinding
import com.sbsj.dreamwing.support.model.response.SupportListResponse
import com.squareup.picasso.Picasso

class SupportRecyclerViewAdapter(
    private val supportList: MutableList<SupportListResponse>,
    private val context: Context
)  : RecyclerView.Adapter<SupportRecyclerViewAdapter.SupportListViewHolder>() {

         private lateinit var binding: ItemSupportBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportListViewHolder {
        binding = ItemSupportBinding.inflate(LayoutInflater.from(context), parent, false)
        return SupportListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SupportListViewHolder, position: Int) {
        val item = supportList[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int {
        return supportList.size
    }

    class SupportListViewHolder(private val binding: ItemSupportBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SupportListResponse) {
            Log.d("SupportRecyclerViewAdapter", item.toString())
            binding.title.text = item.title
            binding.amount.text = item.goalPoint.toString()

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

        private fun calculateTimeRemaining(dDay: String): String {
            val trimmedDay = dDay.trimStart('-')
            val parts = trimmedDay.split(" ")
            val days = parts[0]
            val timeParts = parts[1].split(":")
            val hours = timeParts[0]

            return "$days 일  $hours 시간"
        }

        private fun calculateProgressPercentage(goalPoint: Int, currentPoint: Int): Int {
            return if (goalPoint == 0) {
                0
            } else {
                (currentPoint * 100) / goalPoint
            }
        }
    }
}