package com.andlill.jld.app.dictionarydetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jld.io.repository.CollectionRepository
import com.andlill.jld.io.repository.DictionaryRepository
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DictionaryDetailsViewModel : ViewModel() {

    private var dictionaryEntry = MutableLiveData<DictionaryEntry>()
    private val collections = MutableLiveData<ArrayList<Collection>>()

    fun initialize(context: Context, id: Int) = runBlocking {
        val dictionaryData = DictionaryRepository.getEntry(id)
        dictionaryEntry.value = dictionaryData
        val collectionsData = CollectionRepository.getAll(context)
        collections.value = collectionsData
    }

    fun getDictionaryEntry() : LiveData<DictionaryEntry> {
        return dictionaryEntry
    }

    fun getCollections() : LiveData<ArrayList<Collection>> {
        return collections
    }

    fun updateCollection(context: Context, collection: Collection) = viewModelScope.launch {
        CollectionRepository.update(context, collection)

        val data = CollectionRepository.getAll(context)
        collections.postValue(data)
    }
}