package com.andlill.jld.io.database

import androidx.room.TypeConverter
import com.andlill.jld.model.Collection
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object DataConverter {

    @TypeConverter
    @JvmStatic
    fun convertToJson(value: ArrayList<Int>): String {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun convertToCollectionItemList(value: String): ArrayList<Int> {
        val type = object : TypeToken<ArrayList<Int>>(){}.type
        return Gson().fromJson(value, type)
    }
}