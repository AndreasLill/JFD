package com.andlill.jfd.io.database.dictionary

import androidx.room.TypeConverter

object ConverterListInteger {

    @TypeConverter
    @JvmStatic
    fun convertToList(value: String): List<Int> {
        if (value.isEmpty())
            return ArrayList()

        return value.split("|").map { it.toInt() }
    }

    @TypeConverter
    @JvmStatic
    fun convertFromList(list: List<Int>): String {
        return list.joinToString("|")
    }
}