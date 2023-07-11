package com.andlill.jfd.app.main

import android.content.Context
import android.content.res.AssetManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jfd.language.Dictionary
import com.andlill.jfd.language.KanjiDictionary
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.io.repository.SearchHistoryRepository
import com.andlill.jfd.model.SearchHistory
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.InputStream

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

    fun isDictionaryReady() : Boolean {
        return !Dictionary.isEmpty() && !KanjiDictionary.isEmpty()
    }

    // Load assets if required, callback when complete.
    fun loadAssets(assets: AssetManager, callback: () -> Unit) {
        isLoading = true
        val dictStream: InputStream? = when { Dictionary.isEmpty() -> assets.open("JMdict_e.xml") else -> null }
        val kanjiStream: InputStream? = when { KanjiDictionary.isEmpty() -> assets.open("kanjidic2.xml") else -> null }

        viewModelScope.launch {
            DictionaryRepository.loadData(dictStream, kanjiStream)
            callback()
            isLoading = false
        }
    }
}