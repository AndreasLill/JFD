package com.andlill.jld.app.dictionarydetails

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andlill.jld.io.repository.CollectionRepository
import com.andlill.jld.io.repository.DictionaryRepository
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry
import com.andlill.jld.model.Kanji
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class DictionaryDetailsViewModel : ViewModel() {

    private val scope = CoroutineScope(Dispatchers.Main)
    private var dictionaryEntry = MutableLiveData<DictionaryEntry>()

    fun initialize(id: Int) = runBlocking {
        val data = DictionaryRepository.getEntry(id)
        dictionaryEntry.value = data
    }

    fun getEntry() : LiveData<DictionaryEntry> {
        return dictionaryEntry
    }

    fun getCollections(context: Context) : ArrayList<Collection> = runBlocking {
        return@runBlocking CollectionRepository.getAll(context)
    }

    fun updateCollection(context: Context, collection: Collection) {
        scope.launch {
            CollectionRepository.update(context, collection)
        }
    }
}