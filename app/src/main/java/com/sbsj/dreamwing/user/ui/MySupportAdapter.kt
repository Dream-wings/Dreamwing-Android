package com.sbsj.dreamwing.user.ui

import android.view.LayoutInflater
import com.sbsj.dreamwing.R
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.user.model.vo.MySupportVO

/**
 * 회원의 후원 내역을 RecyclerView에 표시하기 위한 어댑터 클래스
 * @author 정은찬
 * @since 2024.08.04
 *
 * <pre>
 * 수정일        	수정자       				    수정내용
 * ----------  ----------------    --------------------------------------------------------------------------
 *  2024.08.04     	정은찬        		        최초 생성
 * </pre>
 */
class MySupportAdapter : RecyclerView.Adapter<MySupportAdapter.SupportViewHolder>() {

    private val supports = mutableListOf<MySupportVO>()

    fun addSupports(newSupports: List<MySupportVO>) {
        supports.addAll(newSupports)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_support, parent, false)
        return SupportViewHolder(view)
    }

    override fun onBindViewHolder(holder: SupportViewHolder, position: Int) {
        holder.bind(supports[position])
    }

    override fun getItemCount(): Int = supports.size

    class SupportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val title: TextView = itemView.findViewById(R.id.title)
        private val point: TextView = itemView.findViewById(R.id.point)
        private val createdDate: TextView = itemView.findViewById(R.id.createdDate)

        fun bind(mySupportVO: MySupportVO) {
            title.text = mySupportVO.title
            point.text = mySupportVO.point.toString()
            createdDate.text = mySupportVO.createdDate
        }
    }
}