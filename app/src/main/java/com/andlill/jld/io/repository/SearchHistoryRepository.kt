package com.andlill.jld.io.repository

import android.content.Context
import com.andlill.jld.io.database.AppDatabase
import com.andlill.jld.model.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

object SearchHistoryRepository {

    suspend fun getAll(context: Context) = withContext(Dispatchers.IO) {
        return@withContext AppDatabase.database(context).searchHistory().getAll() as ArrayList<SearchHistory>
    }

    suspend fun update(context: Context, query: String) = withContext(Dispatchers.IO) {
        var searchHistory = AppDatabase.database(context).searchHistory().find(query)
        if (searchHistory == null) {
            searchHistory = SearchHistory().apply {
                id = 0
                value = query
            }
        }
        searchHistory.created = Calendar.getInstance().timeInMillis
        AppDatabase.database(context).searchHistory().insertAndLimit(searchHistory)
    }

    suspend fun delete(context: Context, searchHistory: SearchHistory) = withContext(Dispatchers.IO) {
        AppDatabase.database(context).searchHistory().delete(searchHistory)
    }
}