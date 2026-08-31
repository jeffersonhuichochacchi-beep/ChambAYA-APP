package com.example.chambaya

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat

object IdiomaManager {

    private const val PREFS_NAME = "app_language"
    private const val KEY_LANGUAGE = "language"
    private const val SPANISH = "es"
    private const val ENGLISH = "en"

    fun applySavedLanguage(context: Context) {
        val language = getSavedLanguage(context)
        val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
        if (current != language) {
            AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(language))
        }
    }

    fun toggleLanguage(context: Context) {
        val nextLanguage = if (getSavedLanguage(context) == SPANISH) ENGLISH else SPANISH
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_LANGUAGE, nextLanguage)
            .apply()
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(nextLanguage))
    }

    private fun getSavedLanguage(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_LANGUAGE, SPANISH) ?: SPANISH
    }
}
