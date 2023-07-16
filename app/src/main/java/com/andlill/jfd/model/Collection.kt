package com.andlill.jfd.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.andlill.jfd.io.database.user.DataConverter
import java.io.Serializable

@Entity(tableName = "Collection")
class Collection : Serializable {

    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
    var name: String = ""
    var created: Long = 0

    @TypeConverters(DataConverter::class)
    var content: ArrayList<Int> = ArrayList()
}