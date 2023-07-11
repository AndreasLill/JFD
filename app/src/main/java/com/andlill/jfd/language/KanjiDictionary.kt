package com.andlill.jfd.language

import com.andlill.jfd.model.Kanji
import dev.esnault.wanakana.core.Wanakana

object KanjiDictionary {
    private var data = HashMap<String, Kanji>()

    fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    fun setData(data: HashMap<String, Kanji>) {
        KanjiDictionary.data = data
    }

    fun getKanji(word: String): ArrayList<Kanji> {
        val kanjiList = ArrayList<Kanji>()

        for (char in word) {
            if (!Wanakana.isKanji(char))
                continue

            val entry = data[char.toString()]

            if (entry != null)
                kanjiList.add(entry)
            else
                throw Exception("Could not find kanji!")
        }

        return kanjiList
    }

}