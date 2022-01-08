package com.andlill.jfd.io.database.dao

import androidx.room.*
import com.andlill.jfd.model.Collection

@Dao
interface CollectionDAO {

    @Query("SELECT * FROM Collection ORDER BY created")
    suspend fun getAll(): List<Collection>

    @Query("SELECT * FROM Collection WHERE id = :id")
    suspend fun get(id: Long): Collection

    @Insert
    suspend fun insert(entity: Collection): Long

    @Update
    suspend fun update(entity: Collection)

    @Delete
    suspend fun delete(entity: Collection)
}