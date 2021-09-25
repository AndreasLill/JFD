package com.andlill.jld.app.collectiondetails

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

class CollectionDetailsViewModel : ViewModel() {

    private var collection = MutableLiveData<Collection>()

    fun initialize(context: Context, id: Long) = runBlocking {
        val data = CollectionRepository.get(context, id)
        collection.value = data
    }

    fun getCollection(): LiveData<Collection> {
        return collection
    }

    fun getDictionaryEntry(entryId: Int) : DictionaryEntry = runBlocking {
        return@runBlocking DictionaryRepository.getEntry(entryId)
    }

    fun renameCollection(context: Context, name: String) = viewModelScope.launch {
        val data = collection.value as Collection
        data.name = name
        CollectionRepository.update(context, data)
        collection.postValue(data)
    }

    fun removeContent(context: Context, selection: List<Int>) = viewModelScope.launch {
        val data = collection.value as Collection
        selection.forEach {
            data.content.remove(it)
        }
        CollectionRepository.update(context, data)
        collection.postValue(data)
    }
}