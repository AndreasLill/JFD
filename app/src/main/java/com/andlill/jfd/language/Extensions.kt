package com.andlill.jfd.language

import dev.esnault.wanakana.core.Wanakana

object Extensions {

    fun String.containsKanji(): Boolean {
        this.forEach {
            if (Wanakana.isKanji(it))
                return true
        }
        return false
    }
}