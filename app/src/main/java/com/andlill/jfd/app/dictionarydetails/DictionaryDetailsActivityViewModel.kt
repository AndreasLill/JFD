package com.andlill.jfd.app.dictionarydetails

import androidx.lifecycle.ViewModel
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.language.VerbConjugation
import com.andlill.jfd.model.DictionaryEntry
import kotlinx.coroutines.runBlocking

class DictionaryDetailsActivityViewModel : ViewModel() {

    private lateinit var dictionaryEntry: DictionaryEntry
    private lateinit var verbConjugation: VerbConjugation

    fun initialize(id: Int) = runBlocking {
        dictionaryEntry = DictionaryRepository.getEntry(id)
        verbConjugation = DictionaryRepository.getVerbConjugation(id)
    }

    fun getDictionaryEntry(): DictionaryEntry {
        return dictionaryEntry
    }

    fun getVerbConjugation(): VerbConjugation {
        return verbConjugation
    }

    fun getKanji(word: String) = runBlocking {
        return@runBlocking DictionaryRepository.getKanji(word)
    }
}