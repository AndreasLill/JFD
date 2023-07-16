package com.andlill.jfd.io.repository

import android.content.Context
import com.andlill.jfd.io.database.user.UserDatabase
import com.andlill.jfd.model.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.*
import kotlin.collections.ArrayList

object SearchHistoryRepository {

    suspend fun getAll(context: Context) = withContext(Dispatchers.IO) {
        return@withContext UserDatabase.database(context).searchHistory().getAll() as ArrayList<SearchHistory>
    }

    suspend fun update(context: Context, query: String) = withContext(Dispatchers.IO) {
        var searchHistory = UserDatabase.database(context).searchHistory().find(query)
        if (searchHistory == null) {
            searchHistory = SearchHistory().apply {
                id = 0
                value = query
            }
        }
        searchHistory.created = Calendar.getInstance().timeInMillis
        UserDatabase.database(context).searchHistory().insertAndLimit(searchHistory)
    }

    suspend fun delete(context: Context, searchHistory: SearchHistory) = withContext(Dispatchers.IO) {
        UserDatabase.database(context).searchHistory().delete(searchHistory)
    }
}