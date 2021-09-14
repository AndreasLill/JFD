package com.andlill.jld.io.data

import com.andlill.jld.model.Kanji
import com.andlill.jld.utils.LanguageUtils.isKanji

object KanjiDictionary {
    private var data = HashMap<String, Kanji>()

    fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    fun setData(data: HashMap<String, Kanji>) {
        this.data = data
    }

    fun getKanji(word: String): ArrayList<Kanji> {
        val kanjiList = ArrayList<Kanji>()

        for (char in word) {
            if (!char.isKanji())
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