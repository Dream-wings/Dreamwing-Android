package com.sbsj.dreamwing.volunteer.model

/**
 * VolunteerType
 * @author 정은지
 * @since 2024.08.04
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   정은지        최초 생성
 */
enum class VolunteerType(val code: Int, val title: String) {
    VOLUNTEERING(0, "봉사"),
    MENTORING(1, "멘토링");

    companion object {
        fun fromCode(code: Int): String? {
            return values().find { it.code == code }?.title
        }
    }
}