package com.andlill.jfd.model

import androidx.room.*
import java.io.Serializable

data class DictionaryEntry (
    @Embedded
    val entry: DictEntry,

    @Relation(
        parentColumn = "ID",
        entityColumn = "EntryID"
    )
    val reading: List<DictReading>,

    @Relation(
        parentColumn = "ID",
        entityColumn = "EntryID"
    )
    val sense: List<DictSense>
) : Serializable {
    val id: Int inline get() = entry.id
    val commonScore: Int inline get() = entry.commonScore
    val isCommon: Boolean inline get() = entry.commonScore in 1..24
    val primaryReading: String inline get() = when {
        !reading[0].kanji.isNullOrEmpty() -> reading[0].kanji!!
        else -> reading[0].kana
    }
}