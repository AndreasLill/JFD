package com.andlill.jld.io.repository

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

object SharedPreferencesRepository {

    private const val PREFERENCES = "com.andlill.jld.Preferences"
    private const val KEY_DARK_MODE = "com.andlill.jld.DarkMode"

    val OPTIONS_DARK_MODE = arrayOf("System", "On", "Off")

    private lateinit var preferences: SharedPreferences

    private fun getPreferences(context: Context): SharedPreferences {
        return if (this::preferences.isInitialized) preferences
        else context.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getDarkMode(context: Context): String {
        return getPreferences(context).getString(KEY_DARK_MODE, OPTIONS_DARK_MODE[0]) as String
    }

    fun setDarkMode(context: Context, value: String) {
        getPreferences(context).edit {
            putString(KEY_DARK_MODE, value)
            commit()
        }
    }
}