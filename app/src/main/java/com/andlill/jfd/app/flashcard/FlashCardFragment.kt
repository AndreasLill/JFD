package com.andlill.jfd.app.flashcard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.andlill.jfd.R
import com.andlill.jfd.model.DictionaryEntry

class FlashCardFragment(private val entry: DictionaryEntry, private val cardType: Type, val callback: (Action, Int) -> Unit) : Fragment() {

    enum class Type {
        Front,
        Back
    }
    enum class Action {
        Click,
        Dismiss
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_flashcard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val scale = requireContext().resources.displayMetrics.density
        view.cameraDistance = (6000 * scale)

        when (cardType) {
            Type.Front -> {
                view.findViewById<TextView>(R.id.text_kanji).text = entry.primaryReading
            }
            Type.Back -> {
                view.findViewById<View>(R.id.divider).visibility = View.VISIBLE

                entry.reading[0].kanji?.let {
                    view.findViewById<TextView>(R.id.text_reading).text = entry.reading[0].kana
                }

                view.findViewById<TextView>(R.id.text_kanji).text = entry.primaryReading
                view.findViewById<TextView>(R.id.text_glossary).text = entry.sense[0].glossary.joinToString("\n")
            }
        }

        setupCard(view)
    }

    private fun setupCard(view: View) {
        val cardStatusRight = view.findViewById<TextView>(R.id.text_status_correct)
        val cardStatusLeft = view.findViewById<TextView>(R.id.text_status_incorrect)
        val card = view.findViewById<DraggableCardView>(R.id.card)
        card.setOnClickListener {
            callback(Action.Click, 0)
        }
        card.setOnDragCardListener(object: DraggableCardView.OnDragCardListener {
            override fun onDragView() {
                cardStatusRight.alpha = 0f
                cardStatusLeft.alpha = 0f
            }

            override fun onReleaseView() {
            }

            override fun onMoveLeft(progress: Float) {
                cardStatusRight.visibility = View.GONE
                cardStatusLeft.visibility = View.VISIBLE
                cardStatusLeft.alpha = progress
            }

            override fun onMoveRight(progress: Float) {
                cardStatusLeft.visibility = View.GONE
                cardStatusRight.visibility = View.VISIBLE
                cardStatusRight.alpha = progress
            }

            override fun onDismiss(direction: Int) {
                callback(Action.Dismiss, direction)
            }

            override fun onReset() {
                cardStatusRight.visibility = View.GONE
                cardStatusLeft.visibility = View.GONE
                cardStatusRight.alpha = 1f
                cardStatusLeft.alpha = 1f
            }
        })
    }

    fun getCardView(): View {
        return requireView().findViewById(R.id.card)
    }
}