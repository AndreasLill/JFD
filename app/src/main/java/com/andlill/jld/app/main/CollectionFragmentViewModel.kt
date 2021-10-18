package com.andlill.jld.app.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jld.io.data.Dictionary
import com.andlill.jld.io.data.KanjiDictionary
import com.andlill.jld.io.repository.CollectionRepository
import com.andlill.jld.model.Collection
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*

class CollectionFragmentViewModel : ViewModel() {

    private val collectionList = MutableLiveData<ArrayList<Collection>>()

    fun initialize(context: Context) = runBlocking {
        val data = CollectionRepository.getAll(context)
        collectionList.value = data
    }

    fun isDictionaryReady(): Boolean {
        return !Dictionary.isEmpty() && !KanjiDictionary.isEmpty()
    }

    fun getCollection(id: Long) : Collection {
        return collectionList.value?.find { x -> x.id == id } as Collection
    }

    fun getCollections() : LiveData<ArrayList<Collection>> {
        return collectionList
    }

    fun createCollection(context: Context, name: String) = viewModelScope.launch {
        val collection = Collection()
        collection.name = name.trim()
        collection.created = Calendar.getInstance().timeInMillis
        collection.id = CollectionRepository.insert(context, collection)

        val data = CollectionRepository.getAll(context)
        collectionList.postValue(data)
    }

    fun addCollection(context: Context, collection: Collection) = viewModelScope.launch {
        collection.id = CollectionRepository.insert(context, collection)

        val data = CollectionRepository.getAll(context)
        collectionList.postValue(data)
    }

    fun deleteCollection(context: Context, collection: Collection, callback: () -> Unit) = viewModelScope.launch {
        CollectionRepository.delete(context, collection)

        val data = CollectionRepository.getAll(context)
        collectionList.postValue(data)
        callback()
    }
}