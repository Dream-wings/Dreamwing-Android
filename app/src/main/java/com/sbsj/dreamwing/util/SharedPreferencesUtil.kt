package com.sbsj.dreamwing.util

import android.content.Context
import android.content.SharedPreferences

/**
 * SharedPreferences 유틸리티 클래스
 * JWT 토큰을 저장하고 가져오는 기능을 제공
 */
object SharedPreferencesUtil {
    private const val PREFS_NAME = "app_prefs"
    private const val TOKEN_KEY = "jwt_token"

    /**
     * SharedPreferences 인스턴스를 가져옵니다.
     * @param context 애플리케이션 컨텍스트
     * @return SharedPreferences 인스턴스
     */
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    /**
     * JWT 토큰을 저장합니다.
     * @param context 애플리케이션 컨텍스트
     * @param token JWT 토큰
     */
    fun saveToken(context: Context, token: String) {
        val prefs = getPreferences(context)
        prefs.edit().putString(TOKEN_KEY, token).apply()
    }

    /**
     * JWT 토큰을 가져옵니다.
     * @param context 애플리케이션 컨텍스트
     * @return JWT 토큰 또는 null
     */
    fun getToken(context: Context): String? {
        val prefs = getPreferences(context)
        return prefs.getString(TOKEN_KEY, null)
    }
}