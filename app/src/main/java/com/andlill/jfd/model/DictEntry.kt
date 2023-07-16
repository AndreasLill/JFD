package com.andlill.jfd.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Entry")
data class DictEntry (
    @PrimaryKey
    @ColumnInfo("ID")
    var id: Int,
    @ColumnInfo("CommonScore")
    var commonScore: Int,
)