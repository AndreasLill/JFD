package com.andlill.jld.language

object Extensions {

    fun Char.isKanji(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
    }

    fun Char.isKana(): Boolean {
        return Character.UnicodeBlock.of(this) == Character.UnicodeBlock.HIRAGANA || Character.UnicodeBlock.of(this) == Character.UnicodeBlock.KATAKANA
    }

    fun String.isKana(): Boolean {
        this.forEach {
            if (!it.isKana())
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
}