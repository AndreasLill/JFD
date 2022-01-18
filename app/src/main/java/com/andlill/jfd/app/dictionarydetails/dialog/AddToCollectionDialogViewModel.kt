package com.andlill.jfd.app.dictionarydetails.dialog

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.andlill.jfd.io.repository.CollectionRepository
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.*
import kotlin.collections.ArrayList

class AddToCollectionDialogViewModel : ViewModel() {

    private lateinit var collections: ArrayList<Collection>
    private lateinit var dictionaryEntry: DictionaryEntry

    fun initialize(context: Context, id: Int) = runBlocking {
        dictionaryEntry = DictionaryRepository.getEntry(id)
        collections = CollectionRepository.getAll(context)
    }

    fun getDictionaryEntry(): DictionaryEntry {
        return dictionaryEntry
    }

    fun getCollections(): ArrayList<Collection> {
        return collections
    }

    fun updateCollection(context: Context, collection: Collection) = viewModelScope.launch {
        CollectionRepository.update(context, collection)
    }

    fun createCollection(context: Context, name: String) = viewModelScope.launch {
        val collection = Collection()
        collection.name = name.trim()
        collection.created = Calendar.getInstance().timeInMillis
        collection.content.add(dictionaryEntry.id)
        CollectionRepository.insert(context, collection)
    }
}