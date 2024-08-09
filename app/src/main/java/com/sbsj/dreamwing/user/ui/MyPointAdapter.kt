package com.sbsj.dreamwing.user.ui

import android.view.LayoutInflater
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.user.model.vo.MyPointVO

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * 회원의 포인트 내역을 RecyclerView에 표시하기 위한 어댑터 클래스
 * @author 정은찬
 * @since 2024.08.04
 *
 * <pre>
 * 수정일        	수정자       				    수정내용
 * ----------  ----------------    --------------------------------------------------------------------------
 *  2024.08.04     	정은찬        		        최초 생성
 * </pre>
 */
class MyPointAdapter : RecyclerView.Adapter<MyPointAdapter.PointViewHolder>() {

    private val points = mutableListOf<MyPointVO>()

    fun addPoints(newPoints: List<MyPointVO>) {
        points.addAll(newPoints)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point, parent, false)
        return PointViewHolder(view)
    }

    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(points[position])
    }

    override fun getItemCount(): Int = points.size

    class PointViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val activityTitle: TextView = itemView.findViewById(R.id.activityTitle)
        private val point: TextView = itemView.findViewById(R.id.point)
        private val createdDate: TextView = itemView.findViewById(R.id.createdDate)

        fun bind(pointVO: MyPointVO) {
            activityTitle.text = pointVO.activityTitle

            var userPoint = pointVO.point

            if(userPoint >= 0) {
                point.text = "+ " + pointVO.point.toString()
            }
            else {
                point.text = pointVO.point.toString()
            }

            createdDate.text = pointVO.createdDate
        }
    }
}