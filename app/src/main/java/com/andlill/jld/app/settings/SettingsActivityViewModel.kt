package com.andlill.jld.app.settings

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andlill.jld.io.repository.SharedPreferencesRepository

class SettingsActivityViewModel : ViewModel() {

    private val darkMode = MutableLiveData<String>()

    fun initialize(context: Context) {
        darkMode.value = SharedPreferencesRepository.getDarkMode(context)
    }

    fun getDarkMode(): LiveData<String> {
        return darkMode
    }

    fun setDarkMode(context: Context, value: String) {
        SharedPreferencesRepository.setDarkMode(context, value)
        darkMode.postValue(value)
    }
}