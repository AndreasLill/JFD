package com.andlill.jld.app.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andlill.jld.utils.AppConstants

class SettingsActivityViewModel : ViewModel() {

    private lateinit var sharedPreferences: SharedPreferences

    val optionsDarkMode = linkedMapOf(
            "System" to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            "On" to AppCompatDelegate.MODE_NIGHT_YES,
            "Off" to AppCompatDelegate.MODE_NIGHT_NO,
    )

    private val darkMode = MutableLiveData<String>()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(AppConstants.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        darkMode.value = optionsDarkMode.filterValues { it == sharedPreferences.getInt(AppConstants.KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }.keys.first()
    }

    fun getDarkMode(): LiveData<String> {
        return darkMode
    }

    fun setDarkMode(selected: String) {
        val value = optionsDarkMode[selected] as Int
        sharedPreferences.edit {
            putInt(AppConstants.KEY_DARK_MODE, value)
            commit()
        }
        darkMode.postValue(selected)
    }
}