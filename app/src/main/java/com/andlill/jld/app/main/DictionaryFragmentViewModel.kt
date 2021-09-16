package com.andlill.jld.app.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jld.io.repository.DictionaryRepository
import com.andlill.jld.model.DictionaryEntry
import kotlinx.coroutines.launch

class DictionaryFragmentViewModel : ViewModel() {

    private val dictionaryResult = MutableLiveData<ArrayList<DictionaryEntry>>()

    fun getDictionaryResult() : LiveData<ArrayList<DictionaryEntry>> {
        return dictionaryResult
    }

    fun searchDictionary(query: String, callback: () -> Unit) = viewModelScope.launch {
        val data = DictionaryRepository.search(query.trim())
        dictionaryResult.postValue(data)
        callback()
    }
}