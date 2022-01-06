package com.andlill.jld.app.settings

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andlill.jld.utils.AppSettings

class SettingsActivityViewModel : ViewModel() {

    private lateinit var sharedPreferences: SharedPreferences

    val optionsDarkMode = linkedMapOf(
            "System" to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
            "On" to AppCompatDelegate.MODE_NIGHT_YES,
            "Off" to AppCompatDelegate.MODE_NIGHT_NO,
    )

    private val darkMode = MutableLiveData<String>()
    private val textToSpeech = MutableLiveData<Boolean>()

    fun initialize(context: Context) {
        sharedPreferences = context.getSharedPreferences(AppSettings.SHARED_PREFERENCES, Context.MODE_PRIVATE)
        darkMode.value = optionsDarkMode.filterValues { it == sharedPreferences.getInt(AppSettings.KEY_DARK_MODE, AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) }.keys.first()
        textToSpeech.value = sharedPreferences.getBoolean(AppSettings.KEY_TEXT_TO_SPEECH, true)
    }

    fun getDarkMode(): LiveData<String> {
        return darkMode
    }

    fun getTextToSpeech(): LiveData<Boolean> {
        return textToSpeech
    }

    fun setDarkMode(selected: String) {
        val value = optionsDarkMode[selected] as Int
        sharedPreferences.edit {
            putInt(AppSettings.KEY_DARK_MODE, value)
            commit()
        }
        darkMode.postValue(selected)
    }

    fun setTextToSpeech(value: Boolean) {
        sharedPreferences.edit {
            putBoolean(AppSettings.KEY_TEXT_TO_SPEECH, value)
            commit()
        }
        textToSpeech.postValue(value)
    }
}