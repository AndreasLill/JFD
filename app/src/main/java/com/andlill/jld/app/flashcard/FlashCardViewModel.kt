package com.andlill.jld.app.flashcard

import androidx.lifecycle.ViewModel
import com.andlill.jld.io.repository.DictionaryRepository
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

class FlashCardViewModel : ViewModel() {

    private val flashCards = ArrayList<DictionaryEntry>()
    private val flashCardsDone = ArrayList<DictionaryEntry>()
    var canFlip = false
    var stage = 1

    fun initialize(collection: Collection) = runBlocking {
        collection.content.forEach { id ->
            flashCards.add(DictionaryRepository.getEntry(id))
        }
    }

    fun getFlashCards() : ArrayList<DictionaryEntry> {
        return flashCards
    }

    fun getFlashCardsDone() : ArrayList<DictionaryEntry> {
        return flashCardsDone
    }

    fun restart() {
        // Reset all cards.
        if (flashCardsDone.size > 0) {
            flashCards.addAll(flashCardsDone)
            flashCardsDone.clear()
        }

        // Shuffle cards.
        flashCards.shuffle()
    }

    fun undoCard() {
        // Move the card from "done" list.
        val card = flashCardsDone.removeFirst()
        flashCards.add(0, card)
    }

    fun redoCard() {
        // Redo card to study again later.
        val card = flashCards.removeFirst()
        flashCards.add(card)
    }

    fun dismissCard() {
        // Dismiss the card to "done" list.
        val card = flashCards.removeFirst()
        flashCardsDone.add(0, card)
    }
}