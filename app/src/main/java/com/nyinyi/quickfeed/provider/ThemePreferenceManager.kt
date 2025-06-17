package com.nyinyi.quickfeed.provider

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class ThemePreferenceManager(
    context: Context,
) {
    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "theme_prefs"
        private const val KEY_IS_DARK_MODE = "is_dark_mode"
    }

    fun saveDarkModeStatus(isDarkMode: Boolean) {
        sharedPreferences.edit {
            putBoolean(KEY_IS_DARK_MODE, isDarkMode)
        }
    }

    fun getDarkModeStatus(): Boolean =
        sharedPreferences.getBoolean(
            KEY_IS_DARK_MODE,
            false,
        )
}
