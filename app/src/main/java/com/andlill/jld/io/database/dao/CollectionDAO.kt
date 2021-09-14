package com.andlill.jld.io.database.dao

import androidx.room.*
import com.andlill.jld.model.Collection
import kotlinx.coroutines.flow.Flow

@Dao
interface CollectionDAO {

    @Query("SELECT * FROM Collection ORDER BY created DESC") // ORDER BY name COLLATE NOCASE
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