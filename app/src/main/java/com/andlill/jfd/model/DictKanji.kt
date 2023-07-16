package com.andlill.jfd.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("Kanji")
data class DictKanji(
    @PrimaryKey
    @ColumnInfo("Character")
    val character: String,
    @ColumnInfo("Meaning")
    val meaning: String?
)
