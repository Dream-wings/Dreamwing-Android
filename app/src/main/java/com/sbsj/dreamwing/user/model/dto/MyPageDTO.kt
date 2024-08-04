package com.sbsj.dreamwing.user.model.dto

import com.sbsj.dreamwing.user.model.vo.MyVolunteerVO
import com.sbsj.dreamwing.user.model.vo.UserPointVO
import com.sbsj.dreamwing.user.model.vo.UserSupportVO

data class MyPageDTO(
    val name: String, // 사용자 이름
    val phone: String, // 사용자 전화번호
    val profileImageUrl: String, // 프로필 이미지 URL
    val totalPoint: Int, // 총 포인트
    val totalSupportPoint: Int, // 총 지원 포인트
)