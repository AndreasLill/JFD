package com.andlill.jfd.io.database.dao

import androidx.room.*
import com.andlill.jfd.model.SearchHistory

@Dao
interface SearchHistoryDAO {

    @Query("SELECT * FROM SearchHistory ORDER BY created DESC")
    suspend fun getAll(): List<SearchHistory>

    @Query("SELECT * FROM SearchHistory WHERE value = :value")
    suspend fun find(value: String): SearchHistory?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(history: SearchHistory): Long

    @Delete
    suspend fun delete(history: SearchHistory)

    @Query("DELETE FROM SearchHistory WHERE id NOT IN (SELECT id FROM SearchHistory ORDER BY created DESC LIMIT 20)")
    suspend fun limit()

    @Transaction
    suspend fun insertAndLimit(history: SearchHistory) {
        insert(history)
        limit()
    }
}