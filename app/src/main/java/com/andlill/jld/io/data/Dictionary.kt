package com.andlill.jld.io.data

import com.andlill.jld.model.DictionaryEntry
import com.andlill.jld.utils.LanguageUtils.containsKanji
import com.andlill.jld.utils.LanguageUtils.isKana
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
            query.containsKanji() -> ArrayList(searchByKanji(query))
            query.isKana() -> ArrayList(searchByKana(query))
            else -> ArrayList(searchByGlossary(query))
        }
    }

    private fun searchByKanji(query: String): List<DictionaryEntry> {
        // Filter map by kanji value.
        val filteredMap = data.filterValues{ entity -> if (entity.reading[0].kanji.isNotEmpty()) {entity.reading[0].kanji.startsWith(query)} else false }
        return filteredMap.values.toList().sortedWith(compareBy<DictionaryEntry>{ it.commonScore }.thenBy{ it.reading[0].kanji.length })
    }

    private fun searchByKana(query: String): List<DictionaryEntry> {
        // Filter map by kana value.
        val filteredMap = data.filterValues{ entity -> entity.reading[0].kana.startsWith(query) }
        return filteredMap.values.toList().sortedWith(compareBy<DictionaryEntry>{ it.commonScore }.thenBy{ it.reading[0].kana.length })
    }

    private fun searchByGlossary(query: String): List<DictionaryEntry> {
        // Filter map by glossary value.
        val filteredMap = data.filterValues{ entity -> entity.sense[0].glossary.any{ glossary -> (glossary.equals(query, true) || glossary.startsWith("$query ", true) || glossary.endsWith(" $query", true))}}
        return filteredMap.values.toList().sortedWith(compareByDescending<DictionaryEntry>{ it.isCommon() }.thenByDescending{ it.reading[0].priority.size }.thenBy { it.commonScore })
    }
}