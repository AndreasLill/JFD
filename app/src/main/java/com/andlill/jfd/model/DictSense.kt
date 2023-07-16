package com.andlill.jfd.model

import androidx.room.*
import com.andlill.jfd.io.database.dictionary.ConverterListInteger
import com.andlill.jfd.io.database.dictionary.ConverterListString

@Entity(
    tableName = "Sense",
    foreignKeys = [
        ForeignKey(
            entity = DictEntry::class,
            parentColumns = ["ID"],
            childColumns = ["EntryID"]
        )
    ],
    indices = [
        Index(
            name = "IX_Sense_EntryID",
            value = ["EntryID"]
        )
    ]
)
data class DictSense(
    @PrimaryKey
    @ColumnInfo("ID")
    var id: Int,
    @ColumnInfo("EntryID")
    var entryId: Int,
    @ColumnInfo("Glossary")
    @TypeConverters(ConverterListString::class)
    var glossary: List<String>,
    @ColumnInfo("PartOfSpeech")
    @TypeConverters(ConverterListInteger::class)
    var partOfSpeech: List<Int>,
)
