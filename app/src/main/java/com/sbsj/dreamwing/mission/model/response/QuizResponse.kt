package com.sbsj.dreamwing.mission.model.response

import java.util.Date

/**
 * 데일리 퀴즈 응답 데이터 클래스
 * @author 정은지
 * @since 2024.08.02
 * @version 1.0
 *
 * 수정일        	수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.02   정은지        최초 생성
 */
data class QuizResponse(
    val quizId : Long,
    val question : String,
    val answer : Int,
    val choice1 : String,
    val choice2 : String,
    val choice3 : String,
    val choice4 : String,
    val quizDate : Date
)
