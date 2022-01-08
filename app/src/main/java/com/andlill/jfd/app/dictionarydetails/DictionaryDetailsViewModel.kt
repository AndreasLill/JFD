package com.andlill.jfd.app.dictionarydetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jfd.io.repository.CollectionRepository
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import com.andlill.jfd.language.VerbConjugation
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DictionaryDetailsViewModel : ViewModel() {

    private val dictionaryEntry = MutableLiveData<DictionaryEntry>()
    private val collections = MutableLiveData<ArrayList<Collection>>()
    private lateinit var verbConjugation : VerbConjugation

    fun initialize(context: Context, id: Int) = runBlocking {
        dictionaryEntry.value = DictionaryRepository.getEntry(id)
        collections.value = CollectionRepository.getAll(context)
        verbConjugation = DictionaryRepository.getVerbConjugation(id)
    }

    fun getDictionaryEntry(): LiveData<DictionaryEntry> {
        return dictionaryEntry
    }

    fun getCollections(): LiveData<ArrayList<Collection>> {
        return collections
    }

    fun getVerbConjugation(): VerbConjugation {
        return verbConjugation
    }

    fun getKanji(word: String) = runBlocking {
        return@runBlocking DictionaryRepository.getKanji(word)
    }

    fun updateCollection(context: Context, collection: Collection) = viewModelScope.launch {
        CollectionRepository.update(context, collection)

        val data = CollectionRepository.getAll(context)
        collections.postValue(data)
    }
}