package com.andlill.jfd.io.database.dictionary

import androidx.room.TypeConverter

object ConverterListString {

    @TypeConverter
    @JvmStatic
    fun convertToList(value: String): List<String> {
        return value.split("|")
    }

    @TypeConverter
    @JvmStatic
    fun convertFromList(list: List<String>): String {
        return list.joinToString("|")
    }
}