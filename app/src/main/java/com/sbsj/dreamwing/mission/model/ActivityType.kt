package com.sbsj.dreamwing.mission.model

enum class ActivityType(val type: Int, val title: String, val point : Int) {
    VOLUNTEERING(1, "봉사", 5000),
    MENTORING(2, "멘토링", 5000),
    WALK_4000(3, "드림워크 4,000 걸음", 30),
    WALK_7000(3, "드림워크 7,000 걸음", 50),
    WALK_10000(3, "드림워크 10,000 걸음", 100),
    QUIZ(4, "데일리 퀴즈", 30),
    SUPPORT(5, "후원", 0)
}