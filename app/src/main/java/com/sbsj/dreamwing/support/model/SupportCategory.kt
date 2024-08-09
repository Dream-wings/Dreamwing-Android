package com.sbsj.dreamwing.support.model
/**
 * 후원 카테고리를 나타내는 열거형(enum) 클래스
 * 각 카테고리는 화면에 표시될 이름(displayName)을 가지고 있으며, 숫자 값으로 변환할 수 있다.
 * @author 임재성
 * @since 2024.08.01
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   임재성        최초 생성
 */
enum class SupportCategory(val displayName: String) {
    EDUCATION("교육"),
    SUPPLIES("물품"),
    ARTS("문화예술"),
    HOUSING("주거개선"),
    OTHER("기타");

    companion object {
        fun fromValue(value: Int): SupportCategory {
            return when (value) {
                1 -> EDUCATION
                2 -> SUPPLIES
                3 -> ARTS
                4 -> HOUSING
                else -> OTHER
            }
        }
    }
}
