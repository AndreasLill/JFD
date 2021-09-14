package com.andlill.jld.io.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andlill.jld.io.database.dao.CollectionDAO
import com.andlill.jld.io.database.dao.SearchHistoryDAO
import com.andlill.jld.model.Collection
import com.andlill.jld.model.SearchHistory

@Database(entities = [Collection::class, SearchHistory::class], version = 2, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun collection(): CollectionDAO
    abstract fun searchHistory(): SearchHistoryDAO

    companion object {
        private lateinit var db: AppDatabase

        fun database(context: Context): AppDatabase {
            if (!this::db.isInitialized) {
                //TODO: Add migrations in future versions: https://developer.android.com/training/data-storage/room/migrating-db-versions
                db = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "app-database").fallbackToDestructiveMigration().build()
            }

            return db
        }
    }
}