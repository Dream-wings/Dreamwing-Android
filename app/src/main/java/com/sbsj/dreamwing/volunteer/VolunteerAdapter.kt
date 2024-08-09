package com.sbsj.dreamwing.volunteer

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sbsj.dreamwing.databinding.ItemVolunteerBinding
import com.sbsj.dreamwing.volunteer.model.VolunteerListDTO
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * 봉사활동 목록 어댑터
 * @author 임재성
 * @param volunteers 봉사활동 데이터 리스트
 * @param onItemClick 아이템 클릭 시 호출되는 람다 함수
 * @since 2024.08.01
 * @version 1.0
 */
class VolunteerAdapter(
    private val volunteers: List<VolunteerListDTO>,
    private val onItemClick: (VolunteerListDTO) -> Unit
) : RecyclerView.Adapter<VolunteerAdapter.VolunteerViewHolder>() {


    // 봉사활동 뷰 홀더
    inner class VolunteerViewHolder(private val binding: ItemVolunteerBinding) : RecyclerView.ViewHolder(binding.root) {
        // 봉사활동 데이터를 뷰에 바인딩
        fun bind(volunteer: VolunteerListDTO) {
            Log.d("VolunteerAdapter", "Binding volunteer with ID: ${volunteer.volunteerId}")

            with(binding) {
                titleTextView.text = volunteer.title
                categoryTextView.text = volunteer.getCategoryName()
                addressTextView.text = volunteer.address
                volunteerStartDateTextView.text = volunteer.getFormattedStartDate()
                statusTextView.text = if (volunteer.status == 1) "모집완료" else "모집중"
                setImage(volunteer.imageUrl)
                root.setOnClickListener { onItemClick(volunteer) }
            }
        }
        // 이미지 설정
        private fun ItemVolunteerBinding.setImage(imageUrl: String) {
            if (imageUrl.isNotEmpty()) {
                Picasso.get().load(imageUrl).into(imageView)
                imageView.visibility = View.VISIBLE
            } else {
                imageView.visibility = View.GONE
            }
        }
        //카테고리 이름 가져오기
        private fun VolunteerListDTO.getCategoryName() = when (category) {
            1 -> "빵만들기"
            2 -> "자막달기"
            3 -> "돌보기"
            4 -> "밑반찬 만들기"
            5 -> "흙공 만들기"
            else -> "기타"
        }
        //봉사 시작 날짜 포맷팅
        private fun VolunteerListDTO.getFormattedStartDate(): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.getDefault())
            val outputFormat = SimpleDateFormat("yyyy-MM-dd (EEE)", Locale.KOREAN)
            val date = inputFormat.parse(volunteerStartDate)
            return outputFormat.format(date)
        }
    }
    // 뷰 홀더 생성
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VolunteerViewHolder {
        val binding = ItemVolunteerBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VolunteerViewHolder(binding)
    }
    //뷰 홀더 바인딩
    override fun onBindViewHolder(holder: VolunteerViewHolder, position: Int) {
        holder.bind(volunteers[position])
    }
    //아이템 개수 반환
    override fun getItemCount(): Int = volunteers.size
}
