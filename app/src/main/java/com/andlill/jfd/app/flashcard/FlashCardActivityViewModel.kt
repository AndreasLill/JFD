package com.andlill.jfd.app.flashcard

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.andlill.jfd.io.repository.DictionaryRepository
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import kotlinx.coroutines.runBlocking

class FlashCardActivityViewModel : ViewModel() {

    private val flashCards = MutableLiveData<ArrayList<DictionaryEntry>>()
    private val flashCardsDismissed = ArrayList<DictionaryEntry>()
    private val summary = MutableLiveData<ArrayList<Pair<DictionaryEntry, Boolean>>>()
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
        flashCards.value = temp
        summary.value = ArrayList()
    }

    fun getSummary(): LiveData<ArrayList<Pair<DictionaryEntry, Boolean>>> {
        return summary
    }

    fun getFlashCards(): LiveData<ArrayList<DictionaryEntry>> {
        return flashCards
    }

    fun getProgressText(): String {
        val format = "%d / %d"
        flashCards.value?.let {
            return String.format(format, flashCardsDismissed.size, flashCardsDismissed.size + it.size)
        }
        return String.format(format, 0, 0)
    }

    fun getProgressAmount(): Int {
        flashCards.value?.let {
            return (flashCardsDismissed.size.toFloat() / (flashCardsDismissed.size + it.size).toFloat() * 100f).toInt()
        }
        return 0
    }

    private fun addToSummary(item: DictionaryEntry, result: Boolean) {
        // Add to summary if not already in summary.
        summary.value?.let { list ->
            if (list.count { it.first == item } == 0) {
                list.add(Pair(item, result))
                summary.value = ArrayList(list.sortedBy { it.second })
            }
        }
    }

    fun redoCard() {
        // Redo the card to study again.
        flashCards.value?.let {
            val card = it.removeFirst()
            addToSummary(card, false)
            flashCards.value = ArrayList(it.plus(card))
        }
    }

    fun dismissCard() {
        // Dismiss the card to "done" list.
        flashCards.value?.let {
            val card = it.removeFirst()
            addToSummary(card, true)
            flashCardsDismissed.add(0, card)
            flashCards.value = it
        }
    }
}