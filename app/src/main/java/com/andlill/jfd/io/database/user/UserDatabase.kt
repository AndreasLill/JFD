package com.andlill.jfd.io.database.user

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.SearchHistory

@Database(entities = [Collection::class, SearchHistory::class], version = 2, exportSchema = false)
@TypeConverters(DataConverter::class)
abstract class UserDatabase : RoomDatabase() {

    abstract fun collection(): CollectionDAO
    abstract fun searchHistory(): SearchHistoryDAO

    companion object {
        private lateinit var db: UserDatabase

        fun database(context: Context): UserDatabase {
            if (!this::db.isInitialized) {
                //TODO: Add migrations in future versions: https://developer.android.com/training/data-storage/room/migrating-db-versions
                db = Room.databaseBuilder(context.applicationContext, UserDatabase::class.java, "app-database").fallbackToDestructiveMigration().build()
            }

            return db
        }
    }
}