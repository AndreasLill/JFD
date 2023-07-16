package com.andlill.jfd.app.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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