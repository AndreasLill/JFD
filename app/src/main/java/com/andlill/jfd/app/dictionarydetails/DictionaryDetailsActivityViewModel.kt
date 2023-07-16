package com.andlill.jfd.app.dictionarydetails

import android.content.Context
import androidx.lifecycle.ViewModel
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.language.VerbConjugation
import com.andlill.jfd.model.DictionaryEntry
import dev.esnault.wanakana.core.Wanakana
import kotlinx.coroutines.runBlocking

class DictionaryDetailsActivityViewModel : ViewModel() {

    private lateinit var dictionaryEntry: DictionaryEntry
    private lateinit var verbConjugation: VerbConjugation

    fun initialize(context: Context, id: Int) = runBlocking {
        dictionaryEntry = DictionaryRepository.getEntry(context, id)
        verbConjugation = VerbConjugation(dictionaryEntry)
    }

    fun getDictionaryEntry(): DictionaryEntry {
        return dictionaryEntry
    }

    fun getVerbConjugation(): VerbConjugation {
        return verbConjugation
    }

    fun getKanji(context: Context, word: String) = runBlocking {
        val characters = ArrayList<String>()
        for (char in word) {
            if (!Wanakana.isKanji(char))
                continue
            characters.add(char.toString())
        }

        return@runBlocking DictionaryRepository.getKanji(context, characters)
    }
}