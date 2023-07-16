package com.andlill.jfd.app.collectiondetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jfd.io.repository.CollectionRepository
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class CollectionDetailsActivityViewModel : ViewModel() {

    private var collection = MutableLiveData<Collection>()

    fun initialize(context: Context, id: Long) = runBlocking {
        val data = CollectionRepository.get(context, id)
        collection.value = data
    }

    fun getCollection(): LiveData<Collection> {
        return collection
    }

    fun getDictionaryEntry(context: Context, entryId: Int) : DictionaryEntry = runBlocking {
        return@runBlocking DictionaryRepository.getEntry(context, entryId)
    }

    fun renameCollection(context: Context, name: String) = viewModelScope.launch {
        val data = collection.value as Collection
        data.name = name
        CollectionRepository.update(context, data)
        collection.postValue(data)
    }

    fun removeContent(context: Context, selection: HashMap<Int, Int>, callback: () -> Unit) = viewModelScope.launch {
        val data = collection.value as Collection
        selection.values.forEach {
            data.content.remove(it)
        }
        CollectionRepository.update(context, data)
        callback()
    }

    fun addContent(context: Context, selection: HashMap<Int, Int>, callback: () -> Unit) = viewModelScope.launch {
        val data = collection.value as Collection
        selection.forEach { (index, value) ->
            data.content.add(index, value)
        }
        CollectionRepository.update(context, data)
        callback()
    }
}