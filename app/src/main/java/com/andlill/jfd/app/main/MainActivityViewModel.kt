package com.andlill.jfd.app.main

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jfd.io.database.dictionary.DictionaryDatabase
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.io.repository.SearchHistoryRepository
import com.andlill.jfd.model.SearchHistory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainActivityViewModel : ViewModel() {

    private var searchHistoryList = MutableLiveData<List<SearchHistory>>()
    var isLoading = false
        private set

    fun initialize(context: Context) = runBlocking {
        val data = SearchHistoryRepository.getAll(context)
        searchHistoryList.value = data
    }

    fun initializeDatabase(context: Context) = viewModelScope.launch {
        // Cold start database to improve performance for user interaction.
        DictionaryDatabase.database(context).dictionary().getEntry(9999999)
    }

    fun getSearchHistory(): LiveData<List<SearchHistory>> {
        return searchHistoryList
    }

    fun updateSearchHistory(context: Context, query: String) = viewModelScope.launch {
        SearchHistoryRepository.update(context, query.trim())
        val data = SearchHistoryRepository.getAll(context)
        searchHistoryList.postValue(data)
    }

    fun deleteSearchHistory(context: Context, searchHistory: SearchHistory) = viewModelScope.launch {
        SearchHistoryRepository.delete(context, searchHistory)
        val data = SearchHistoryRepository.getAll(context)
        searchHistoryList.postValue(data)
    }
}