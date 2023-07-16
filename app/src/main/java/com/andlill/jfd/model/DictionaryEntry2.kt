package com.andlill.jfd.model

import androidx.room.*

data class DictionaryEntry2 (
    @Embedded
    val entry: DictEntry,

    @Relation(
        parentColumn = "ID",
        entityColumn = "EntryID"
    )
    val readings: List<DictReading>,

    @Relation(
        parentColumn = "ID",
        entityColumn = "EntryID"
    )
    val senses: List<DictSense>
)