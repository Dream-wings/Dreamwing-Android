package com.sbsj.dreamwing.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences 유틸리티 클래스
 * @author 정은찬
 * @since 2024.08.04
 * @version 1.0
 *
 * 수정일        수정자        수정내용
 * ----------  --------    ---------------------------
 * 2024.08.04   정은찬        최초 생성
 */
object SharedPreferencesUtil {
    private const val PREFS_NAME = "app_prefs"
    private const val TOKEN_KEY = "jwt_token"

    /**
     * SharedPreferences 인스턴스를 가져오는 메서드
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * JWT 토큰을 저장하는 메서드
     */
    fun saveToken(context: Context, token: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    /**
     * JWT 토큰을 가져오는 메서드
     */
    fun getToken(context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(TOKEN_KEY, null)
    }

    /**
     * JWT 토큰을 삭제하는 메서드
     */
    fun clearToken(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().remove(TOKEN_KEY).apply()
    }
}