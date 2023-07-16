package com.andlill.jfd.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.andlill.jfd.io.database.dictionary.ConverterListString

@Entity("Kanji")
data class DictKanji(
    @PrimaryKey
    @ColumnInfo("Character")
    val character: String,
    @ColumnInfo("Meaning")
    @TypeConverters(ConverterListString::class)
    val meaning: List<String>
)
