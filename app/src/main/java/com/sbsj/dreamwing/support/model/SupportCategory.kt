package com.sbsj.dreamwing.support.model

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
