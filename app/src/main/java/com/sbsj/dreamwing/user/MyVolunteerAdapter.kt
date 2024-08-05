package com.sbsj.dreamwing.user

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.R
import com.sbsj.dreamwing.user.model.vo.MyVolunteerVO

class MyVolunteerAdapter : RecyclerView.Adapter<MyVolunteerAdapter.VolunteerViewHolder>() {

    // 봉사 활동 데이터 리스트
    private val volunteers = mutableListOf<MyVolunteerVO>()

    // 새로운 봉사 활동 데이터를 추가하는 함수
    fun addVolunteers(newVolunteers: List<MyVolunteerVO>) {
        volunteers.addAll(newVolunteers)
        notifyDataSetChanged() // 데이터가 변경되었음을 알림
    }

    // ViewHolder를 생성하는 함수
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        // 레이아웃을 인플레이트하여 View를 생성
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_my_volunteer, parent, false)
        return VolunteerViewHolder(view)
    }

    // ViewHolder와 데이터를 바인딩하는 함수
    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        holder.bind(volunteers[position])
    }

    // 아이템 개수를 반환하는 함수
    override fun getItemCount(): Int = volunteers.size

    // ViewHolder 클래스
    class VolunteerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // 레이아웃의 뷰들을 바인딩
        private val title: TextView = itemView.findViewById(R.id.title)
        private val verifiedText: TextView = itemView.findViewById(R.id.verifiedText)
        private val verifiedButton: Button = itemView.findViewById(R.id.verifiedButton)
        private val volunteerEndDate: TextView = itemView.findViewById(R.id.volunteerEndDate)

        // 데이터와 뷰를 바인딩하는 함수
        fun bind(myVolunteerVO: MyVolunteerVO) {
            title.text = myVolunteerVO.title // 봉사 활동 제목 설정

            // 인증 상태에 따라 뷰를 설정
            if (myVolunteerVO.verified == 1) {
                // 인증 완료 상태
                verifiedText.text = "인증 완료"
                verifiedText.visibility = View.VISIBLE // TextView를 보이게 설정
                verifiedButton.visibility = View.GONE // Button을 숨기게 설정
            } else {
                // 인증 필요 상태
                verifiedButton.text = "인증 필요"
                verifiedButton.setBackgroundResource(R.drawable.button_unverified) // 버튼 배경 설정
                verifiedButton.setTextColor(itemView.context.getColor(R.color.white)) // 버튼 텍스트 색상 설정
                verifiedButton.visibility = View.VISIBLE // Button을 보이게 설정
                verifiedText.visibility = View.GONE // TextView를 숨기게 설정
            }

            // 봉사 종료일 설정
            volunteerEndDate.text = myVolunteerVO.volunteerEndDate
        }
    }
}