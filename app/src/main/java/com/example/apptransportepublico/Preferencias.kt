package com.example.apptransportepublico

import android.content.Context
import android.content.SharedPreferences

class Preferencias(context: Context) {
    private val preferences: SharedPreferences =
        context.getSharedPreferences("preferencias", Context.MODE_PRIVATE)

    companion object {
        private const val DARK_THEME_KEY = "dark_theme"
    }

    fun isDarkTheme(): Boolean {
        return preferences.getBoolean(DARK_THEME_KEY, false) // Default to light theme
    }

    fun salvarTema(isDarkTheme: Boolean) {
        preferences.edit().putBoolean(DARK_THEME_KEY, isDarkTheme).apply()
    }
}