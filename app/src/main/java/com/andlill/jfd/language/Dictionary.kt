package com.andlill.jfd.language

import com.andlill.jfd.model.DictionaryEntry
import com.andlill.jfd.language.Extensions.containsKanji
import dev.esnault.wanakana.core.Wanakana
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
        Dictionary.data = data
    }

    fun search(query: String): ArrayList<DictionaryEntry> {
        return when {
            query.containsKanji() -> searchByKanji(query)
            Wanakana.isKana(query) -> searchByKana(query)
            else -> searchByGlossary(query)
        }
    }

    fun conjugateVerb(id: Int): VerbConjugation {
        val entry = data[id] as DictionaryEntry
        return VerbConjugation(entry)
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
        val hiragana = Wanakana.toHiragana(query)
        val katakana = Wanakana.toKatakana(query)
        val filteredMap = data.filterValues{ entity ->
            entity.reading[0].kana.startsWith(hiragana) || entity.reading[0].kana.startsWith(katakana)
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