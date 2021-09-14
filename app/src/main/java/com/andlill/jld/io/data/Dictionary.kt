package com.andlill.jld.io.data

import com.andlill.jld.model.DictionaryEntry
import com.andlill.jld.utils.LanguageUtils.containsKanji
import com.andlill.jld.utils.LanguageUtils.isHiragana
import com.andlill.jld.utils.LanguageUtils.isKana
import com.andlill.jld.utils.LanguageUtils.toHiragana
import com.andlill.jld.utils.LanguageUtils.toKatakana
import kotlin.collections.HashMap

object Dictionary {
    private var data = HashMap<Int, DictionaryEntry>()

    fun isEmpty(): Boolean {
        return data.isEmpty()
    }

    fun getEntry(id: Int): DictionaryEntry {
        return data[id] as DictionaryEntry
    }

    fun setData(data: HashMap<Int, DictionaryEntry>) {
        this.data = data
    }

    fun search(query: String): ArrayList<DictionaryEntry> {
        return when {
            query.containsKanji() -> searchByKanji(query)
            query.isKana() -> searchByKana(query)
            else -> searchByGlossary(query)
        }
    }

    private fun searchByKanji(query: String): ArrayList<DictionaryEntry> {
        val filteredMap = data.filterValues{ entity ->
            if (entity.reading[0].kanji.isNotEmpty())
                entity.reading[0].kanji.startsWith(query)
            else
                false
        }
        val sortedList = filteredMap.values.toList().sortedWith(compareBy<DictionaryEntry>{ it.commonScore }.thenBy{ it.reading[0].kanji.length })
        return ArrayList(sortedList)
    }

    private fun searchByKana(query: String): ArrayList<DictionaryEntry> {
        val converted = if (query.isHiragana()) query.toKatakana() else query.toHiragana()
        val filteredMap = data.filterValues{ entity ->
            entity.reading[0].kana.startsWith(query) ||
            entity.reading[0].kana.startsWith(converted)
        }
        val sortedList = filteredMap.values.toList().sortedWith(compareBy<DictionaryEntry>{ it.commonScore }.thenBy{ it.reading[0].kana.length })
        return ArrayList(sortedList)
    }

    private fun searchByGlossary(query: String): ArrayList<DictionaryEntry> {
        val filteredMap = data.filterValues{ entity ->
            entity.sense[0].glossary.any { glossary ->
                (glossary.equals(query, true) || glossary.startsWith("$query ", true) || glossary.endsWith(" $query", true))
            }
        }
        val sortedList = filteredMap.values.toList().sortedWith(compareByDescending<DictionaryEntry>{ it.isCommon() }.thenByDescending{ it.reading[0].priority.size }.thenBy { it.commonScore })
        return ArrayList(sortedList)
    }
}