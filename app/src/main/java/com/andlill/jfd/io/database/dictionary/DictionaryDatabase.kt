package com.andlill.jfd.io.database.dictionary

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.andlill.jfd.model.DictEntry
import com.andlill.jfd.model.DictKanji
import com.andlill.jfd.model.DictReading
import com.andlill.jfd.model.DictSense

@Database(entities = [DictEntry::class, DictReading::class, DictSense::class, DictKanji::class], version = 1, exportSchema = false)
@TypeConverters(ConverterListString::class, ConverterListInteger::class)
abstract class DictionaryDatabase : RoomDatabase() {

    abstract fun dictionary(): DictionaryDAO

    companion object {
        private lateinit var db: DictionaryDatabase

        fun database(context: Context): DictionaryDatabase {
            if (!this::db.isInitialized) {
                db = Room.databaseBuilder(context, DictionaryDatabase::class.java, "dictionary-database").createFromAsset("dictionary.db").fallbackToDestructiveMigration().build()
            }
            return db
        }
    }
}