package com.andlill.jfd.io.repository

import com.andlill.jfd.language.Dictionary
import com.andlill.jfd.language.KanjiDictionary
import com.andlill.jfd.io.xml.XmlManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import java.io.InputStream

object DictionaryRepository {

    suspend fun loadData(dictionaryInput: InputStream?, kanjiInput: InputStream?) = withContext(Dispatchers.IO) {
        val dictionary = async {
            if (dictionaryInput != null)
                Dictionary.setData(XmlManager.parseJMdict(dictionaryInput))
        }
        val kanji = async {
            if (kanjiInput != null)
                KanjiDictionary.setData(XmlManager.parseKanji(kanjiInput))
        }
        dictionary.await()
        kanji.await()
    }

    suspend fun search(query: String) = withContext(Dispatchers.Default) {
        return@withContext Dictionary.search(query)
    }

    suspend fun getEntry(id: Int) = withContext(Dispatchers.Default) {
        return@withContext Dictionary.getEntry(id)
    }

    suspend fun getKanji(word: String) = withContext(Dispatchers.Default) {
        return@withContext KanjiDictionary.getKanji(word)
    }

    suspend fun getVerbConjugation(id: Int) = withContext(Dispatchers.Default) {
        return@withContext Dictionary.conjugateVerb(id)
    }
}