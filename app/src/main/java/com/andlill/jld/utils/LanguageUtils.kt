package com.andlill.jld.utils

import com.andlill.jld.io.data.KanaDictionary

object LanguageUtils {

    fun Char.isKanji(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    }

    fun Char.isKana(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.HIRAGANA || Character.UnicodeBlock.of(this) == Character.UnicodeBlock.KATAKANA
    }

    fun Char.isHiragana(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.HIRAGANA
    }

    fun Char.isKatakana(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.KATAKANA
    }

    fun String.isRomaji(): Boolean {
        this.forEach {
            if (it.isKanji() || it.isKana())
                return false
        }
        return true
    }

    fun String.isKana(): Boolean {
        this.forEach {
            if (!it.isKana())
                return false
        }
        return true
    }

    fun String.isHiragana(): Boolean {
        this.forEach {
            if (!it.isHiragana())
                return false
        }
        return true
    }

    fun String.isKatakana(): Boolean {
        this.forEach {
            if (!it.isKatakana())
                return false
        }
        return true
    }

    fun String.containsKanji(): Boolean {
        this.forEach {
            if (it.isKanji())
                return true
        }
        return false
    }

    private fun romajiToHiragana(str: String): String {
        var value: String = str
        KanaDictionary.kana.forEach {
            val data = it.split("|")
            val src = data[0]
            val dest = data[1]
            value = value.replace(src, dest)
        }
        return value
    }

    private fun romajiToKatakana(str: String): String {
        var value: String = str
        KanaDictionary.kana.forEach {
            val data = it.split("|")
            val src = data[0]
            val dest = data[2]
            value = value.replace(src, dest)
        }
        return value
    }

    private fun hiraganaToKatakana(str: String): String {
        var value: String = str
        KanaDictionary.kana.forEach {
            val data = it.split("|")
            val src = data[1]
            val dest = data[2]
            value = value.replace(src, dest)
        }
        return value
    }

    private fun katakanaToHiragana(str: String): String {
        var value: String = str
        KanaDictionary.kana.forEach {
            val data = it.split("|")
            val src = data[2]
            val dest = data[1]
            value = value.replace(src, dest)
        }
        return value
    }

    fun String.toHiragana(): String {
        return when {
            this.isRomaji() -> romajiToHiragana(this)
            this.isKatakana() -> katakanaToHiragana(this)
            else -> ""
        }
    }

    fun String.toKatakana(): String {
        return when {
            this.isRomaji() -> romajiToKatakana(this)
            this.isHiragana() -> hiraganaToKatakana(this)
            else -> ""
        }
    }
}