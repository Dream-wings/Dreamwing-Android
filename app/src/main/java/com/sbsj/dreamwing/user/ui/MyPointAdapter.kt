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
    // 포인트 데이터 리스트
    private val points = mutableListOf<MyPointVO>()

    /*
     * 데이터를 추가하고 RecyclerView를 갱신하는 메서드
     */
    fun addPoints(newPoints: List<MyPointVO>) {
        points.addAll(newPoints)
        notifyDataSetChanged()
    }

    /*
     * PointViewHolder를 생성하고 초기화하는 메서드
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PointViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_point, parent, false)
        return PointViewHolder(view)
    }

    /*
     *  PointViewHolder와 데이터를 바인딩하는 메서드
     */
    override fun onBindViewHolder(holder: PointViewHolder, position: Int) {
        holder.bind(points[position])
    }

    override fun getItemCount(): Int = points.size

    /*
     * RecyclerView의 각 아이템을 나타내는 뷰 홀더 클래스
     */
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