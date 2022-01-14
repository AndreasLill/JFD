package com.andlill.jfd.model

import java.io.Serializable

class DictionaryEntry : Serializable {

    var id : Int = 0
    var commonScore: Int = 50
    val reading : ArrayList<Reading> = ArrayList()
    val sense : ArrayList<Sense> = ArrayList()

    class Reading : Serializable {
        var kana : String = ""
        var kanji : String = ""
        var info : ArrayList<String> = ArrayList()
        var priority : ArrayList<String> = ArrayList()
    }

    fun isCommon(): Boolean {
        return commonScore in 1..24
    }

    fun getPrimaryReading(): String {
        return when {
            reading[0].kanji.isNotEmpty() -> reading[0].kanji
            else -> reading[0].kana
        }
    }
}