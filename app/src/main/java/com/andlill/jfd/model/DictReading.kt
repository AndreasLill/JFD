package com.andlill.jfd.model

import androidx.room.*

@Entity(
    tableName = "Reading",
    foreignKeys = [
        ForeignKey(
            entity = DictEntry::class,
            parentColumns = ["ID"],
            childColumns = ["EntryID"]
        )
    ],
    indices = [
        Index(
            name = "IX_Reading_EntryID",
            value = ["EntryID"]
        )
    ]
)
data class DictReading(
    @PrimaryKey
    @ColumnInfo("ID")
    var id: Int,
    @ColumnInfo("EntryID")
    var entryId: Int,
    @ColumnInfo("Kana")
    var kana: String,
    @ColumnInfo("Kanji")
    var kanji: String?
)
