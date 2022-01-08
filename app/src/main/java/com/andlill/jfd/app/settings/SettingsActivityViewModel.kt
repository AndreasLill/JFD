package com.andlill.jfd.app.settings

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andlill.jfd.utils.AppSettings

class SettingsActivityViewModel : ViewModel() {

    val optionsDarkMode = linkedMapOf(
        "System" to AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM,
        "On" to AppCompatDelegate.MODE_NIGHT_YES,
        "Off" to AppCompatDelegate.MODE_NIGHT_NO,
    )

    private val darkMode = MutableLiveData<String>()
    private val textToSpeech = MutableLiveData<Boolean>()

    fun initialize(context: Context) {
        darkMode.value = optionsDarkMode.filterValues { it == AppSettings.getDarkMode(context) }.keys.first()
        textToSpeech.value = AppSettings.getTextToSpeech(context)
    }

    fun getDarkMode(): LiveData<String> {
        return darkMode
    }

    fun getTextToSpeech(): LiveData<Boolean> {
        return textToSpeech
    }

    fun setDarkMode(context: Context, selected: String) {
        val value = optionsDarkMode[selected] as Int
        AppSettings.setDarkMode(context, value)
        darkMode.postValue(selected)
    }

    fun setTextToSpeech(context: Context, value: Boolean) {
        AppSettings.setTextToSpeech(context, value)
        textToSpeech.postValue(value)
    }
}