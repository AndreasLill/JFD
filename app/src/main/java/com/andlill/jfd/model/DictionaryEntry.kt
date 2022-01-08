package com.andlill.jfd.model

import java.io.Serializable

class DictionaryEntry : Serializable {

    var id : Int = 0
    var commonScore: Int = 0
    val reading : ArrayList<Reading> = ArrayList()
    val sense : ArrayList<Sense> = ArrayList()

    class Reading : Serializable {
        var kana : String = ""
        var kanji : String = ""
        var info : ArrayList<String> = ArrayList()
        var priority : ArrayList<String> = ArrayList()
    }

    class Sense : Serializable {
        val partOfSpeech : ArrayList<String> = ArrayList()
        val field : ArrayList<String> = ArrayList()
        val misc : ArrayList<String> = ArrayList()
        val usage : ArrayList<String> = ArrayList()
        val dialect : ArrayList<String> = ArrayList()
        val glossary : ArrayList<String> = ArrayList()
    }

    fun updateCommonScore(value: Int) {
        if (commonScore == 0 || commonScore > value)
            commonScore = value
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