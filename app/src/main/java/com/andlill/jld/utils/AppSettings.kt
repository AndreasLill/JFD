package com.andlill.jld.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object AppSettings {

    private lateinit var preferences: SharedPreferences

    private fun getPreferences(context: Context): SharedPreferences {
        return if (this::preferences.isInitialized) preferences
        else context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
    }

    fun getDarkMode(context: Context): Int {
        return getPreferences(context).getInt(AppConstants.KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
    }
 }