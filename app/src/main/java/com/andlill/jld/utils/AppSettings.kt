package com.andlill.jld.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit

object AppSettings {

    private const val SHARED_PREFERENCES = "com.andlill.jld.Preferences"
    private const val KEY_DARK_MODE = "com.andlill.jld.KeyDarkMode"
    private const val KEY_TEXT_TO_SPEECH = "com.andlill.jld.KeyTextToSpeech"

    private lateinit var preferences: SharedPreferences

    private fun getPreferences(context: Context): SharedPreferences {
        return if (this::preferences.isInitialized) preferences
        else context.getSharedPreferences(SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getDarkMode(context: Context): Int {
        return getPreferences(context).getInt(KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }

    fun setDarkMode(context: Context, value: Int) {
        getPreferences(context).edit {
            putInt(KEY_DARK_MODE, value)
            commit()
        }
    }

    fun getTextToSpeech(context: Context): Boolean {
        return getPreferences(context).getBoolean(KEY_TEXT_TO_SPEECH, true)
    }

    fun setTextToSpeech(context: Context, value: Boolean) {
        getPreferences(context).edit {
            putBoolean(KEY_TEXT_TO_SPEECH, value)
            commit()
        }
    }
 }