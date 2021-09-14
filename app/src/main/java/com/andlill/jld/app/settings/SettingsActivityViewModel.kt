package com.andlill.jld.app.settings

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SettingsActivityViewModel : ViewModel() {

    private var preferences = MutableLiveData<SharedPreferences>()

    fun initialize(pref: SharedPreferences) {
        preferences.value = pref
    }

    fun getSharedPreferences() : LiveData<SharedPreferences> {
        return preferences
    }

    fun getSharedPreferenceString(key: String, default: String) : String {
        return preferences.value?.getString(key, default) as String
    }

    fun setSharedPreferences(key: String, value: String) {
        val data = preferences.value as SharedPreferences
        data.edit {
            putString(key, value)
            commit()
        }
        preferences.postValue(data)
    }
}