package com.andlill.jfd.app.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.language.Extensions.containsKanji
import com.andlill.jfd.model.DictionaryEntry
import dev.esnault.wanakana.core.Wanakana
import kotlinx.coroutines.launch

class DictionaryFragmentViewModel : ViewModel() {

    private val dictionaryResult = MutableLiveData<ArrayList<DictionaryEntry>>()

    fun getDictionaryResult() : LiveData<ArrayList<DictionaryEntry>> {
        return dictionaryResult
    }

    fun searchDictionary(context: Context, query: String, callback: (Int) -> Unit) = viewModelScope.launch {
        val queryTrimmed = query.trim()
        val data = when {
            queryTrimmed.containsKanji() -> searchByKanji(context, queryTrimmed)
            Wanakana.isKana(queryTrimmed) -> searchByKana(context, queryTrimmed)
            else -> searchByGlossary(context, queryTrimmed)
        }
        dictionaryResult.postValue(ArrayList(data))
        callback(data.size)
    }

    private suspend fun searchByKanji(context: Context, query: String): List<DictionaryEntry> {
        val results = DictionaryRepository.searchByKanji(context, query)
        return results.sortedWith(compareByDescending<DictionaryEntry> { it.isCommon }.thenBy { it.reading[0].kanji!!.length })
    }

    private suspend fun searchByKana(context: Context, query: String): List<DictionaryEntry> {
        val hiragana = Wanakana.toHiragana(query)
        val katakana = Wanakana.toKatakana(query)
        val results = DictionaryRepository.searchByKana(context, hiragana, katakana)
        return results.sortedWith(compareByDescending<DictionaryEntry> { it.isCommon }.thenBy { it.reading[0].kana.length })
    }

    private suspend fun searchByGlossary(context: Context, query: String): List<DictionaryEntry> {
        val results = DictionaryRepository.searchByGlossary(context, query)
        return results.sortedWith(compareByDescending<DictionaryEntry> { it.isCommon }.thenBy { it.commonScore })
    }
}