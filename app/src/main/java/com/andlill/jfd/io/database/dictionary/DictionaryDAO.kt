package com.andlill.jfd.io.database.dictionary

import androidx.room.Dao
import androidx.room.Query
import com.andlill.jfd.model.DictionaryEntry2

@Dao
interface DictionaryDAO {
    @Query("SELECT * FROM Entry")
    suspend fun getAll(): List<DictionaryEntry2>
}