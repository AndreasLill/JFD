package com.andlill.jld.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "SearchHistory")
class SearchHistory {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var value: String = ""
    var created: Long = 0
}