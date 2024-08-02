package com.sbsj.dreamwing.mission.model

enum class ActivityType(val type: Int, val displayName: String, val point : Int) {
    VOLUNTEERING(1, "봉사", 5000),
    MENTORING(2, "멘토링", 5000),
    WALK(3, "드림워크", 0),
    QUIZ(4, "데일리 퀴즈", 30),
    SUPPORT(5, "후원", 0)
}