package com.andlill.jfd.io.repository

import android.content.Context
import com.andlill.jfd.io.database.AppDatabase
import com.andlill.jfd.model.Collection
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object CollectionRepository {

    suspend fun get(context: Context, id: Long) = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.database(context).collection().get(id)
    }

    suspend fun getAll(context: Context) = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.database(context).collection().getAll() as ArrayList<Collection>
    }

    suspend fun insert(context: Context, collection: Collection) = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.database(context).collection().insert(collection)
    }

    suspend fun update(context: Context, collection: Collection) = withContext(Dispatchers.IO) {
        AppDatabase.database(context).collection().update(collection)
    }

    suspend fun delete(context: Context, collection: Collection) = withContext(Dispatchers.IO) {
        AppDatabase.database(context).collection().delete(collection)
    }
}