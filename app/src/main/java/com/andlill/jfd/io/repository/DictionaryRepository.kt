package com.andlill.jfd.io.repository

import android.content.Context
import com.andlill.jfd.io.database.dictionary.DictionaryDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object DictionaryRepository {

    suspend fun searchByKanji(context: Context, kanji: String) = withContext(Dispatchers.IO) {
        return@withContext DictionaryDatabase.database(context).dictionary().searchByKanji(kanji)
    }

    suspend fun searchByKana(context: Context, hiragana: String, katakana: String) = withContext(Dispatchers.IO) {
        return@withContext DictionaryDatabase.database(context).dictionary().searchByKana(hiragana, katakana)
    }

    suspend fun searchByGlossary(context: Context, query: String) = withContext(Dispatchers.IO) {
        return@withContext DictionaryDatabase.database(context).dictionary().searchByGlossary(query)
    }

    suspend fun getEntry(context: Context, id: Int) = withContext(Dispatchers.IO) {
        return@withContext DictionaryDatabase.database(context).dictionary().getEntry(id)
    }

    suspend fun getEntry(context: Context, ids: List<Int>) = withContext(Dispatchers.IO) {
        return@withContext DictionaryDatabase.database(context).dictionary().getEntry(ids)
    }

    suspend fun getKanji(context: Context, characters: List<String>) = withContext(Dispatchers.IO) {
        return@withContext DictionaryDatabase.database(context).dictionary().getKanji(characters)
    }
}