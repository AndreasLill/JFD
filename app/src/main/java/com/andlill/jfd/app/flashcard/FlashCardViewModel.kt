package com.andlill.jfd.app.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import kotlinx.coroutines.runBlocking
import java.util.ArrayList

class FlashCardViewModel : ViewModel() {

    private val flashCards = MutableLiveData<ArrayList<DictionaryEntry>>()
    private val flashCardsDismissed = ArrayList<DictionaryEntry>()
    private val flashCardsStaged = ArrayList<DictionaryEntry>()
    private val stage = MutableLiveData<Int>()
    var canFlip = false

    fun initialize(collection: Collection) = runBlocking {
        val temp = ArrayList<DictionaryEntry>()

        // Get dictionary data for each card.
        collection.content.forEach { id ->
            temp.add(DictionaryRepository.getEntry(id))
        }

        // Shuffle cards and add cards to live data.
        temp.shuffle()
        flashCardsDismissed.clear()
        flashCardsStaged.clear()
        flashCards.value = temp
        stage.value = 1
    }

    fun getFlashCards(): LiveData<ArrayList<DictionaryEntry>> {
        return flashCards
    }

    fun getStage(): LiveData<Int> {
        return stage
    }

    fun getStageSize(): Int {
        return flashCardsStaged.size
    }

    fun getProgressText(): String {
        val format = "%d / %d"
        flashCards.value?.let {
            return String.format(format, flashCardsDismissed.size + flashCardsStaged.size, flashCardsDismissed.size + flashCardsStaged.size + it.size)
        }
        return String.format(format, 0, 0)
    }

    fun getProgressAmount(): Int {
        flashCards.value?.let {
            return ((flashCardsDismissed.size + flashCardsStaged.size).toFloat() / (flashCardsDismissed.size + flashCardsStaged.size + it.size).toFloat() * 100f).toInt()
        }
        return 0
    }

    fun nextStage() {
        flashCards.value?.let {
            it.clear()
            it.addAll(flashCardsStaged)
            flashCardsStaged.clear()
            flashCardsDismissed.clear()
            flashCards.value = it
            stage.value?.let { value ->
                stage.value = value.inc()
            }
        }
    }

    fun stageCard() {
        // Stage the card to study again in the next stage.
        flashCards.value?.let {
            val card = it.removeFirst()
            flashCardsStaged.add(card)
            flashCards.value = it
        }
    }

    fun dismissCard() {
        // Dismiss the card to "done" list.
        flashCards.value?.let {
            val card = it.removeFirst()
            flashCardsDismissed.add(0, card)
            flashCards.value = it
        }
    }
}