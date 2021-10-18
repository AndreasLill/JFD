package com.andlill.jld.app.flashcard

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.andlill.jld.R
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry
import com.andlill.jld.utils.AppUtils
import com.google.android.material.progressindicator.LinearProgressIndicator
import java.util.*

class FlashCardActivity : AppCompatActivity() {

    companion object {
        const val ARGUMENT_COLLECTION = "com.andlill.jld.Collection"
    }

    // Flashcard View Model
    private lateinit var viewModel: FlashCardViewModel

    // Views
    private lateinit var undoButton: View
    private lateinit var restartButton: View
    private lateinit var progressText: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var backgroundCard: View
    private lateinit var backgroundCardText: TextView
    private lateinit var textToSpeech: TextToSpeech

    // Variables
    private var soundMuted = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        // Get intent extras.
        val collection = intent.getSerializableExtra(ARGUMENT_COLLECTION) as Collection

        // Setup flashcard view model.
        viewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)
        viewModel.initialize(collection)

        // Setup views.
        progressText = findViewById(R.id.text_progress)
        progressBar = findViewById(R.id.progress_bar)
        backgroundCard = findViewById(R.id.card_background)
        backgroundCardText = findViewById(R.id.text_background)
        restartButton = findViewById<View>(R.id.button_restart).apply { setOnClickListener { viewModel.restart(); this@FlashCardActivity.drawCard() } }
        undoButton = findViewById<View>(R.id.button_undo).apply { setOnClickListener { viewModel.undoCard(); this@FlashCardActivity.drawCard() } }
        findViewById<ImageButton>(R.id.button_sound).apply { setOnClickListener { toggleSound(this) } }
        findViewById<ImageButton>(R.id.button_back).apply { setOnClickListener { finish() } }

        viewModel.restart()
        this.drawCard()
    }

    override fun onResume() {
        super.onResume()
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.JAPANESE
            }
        }

        // Set status bar to dark if not in night mode.
        if (!AppUtils.isDarkMode(this)) {
            AppUtils.setStatusBarDark(this)
        }
    }

    override fun onPause() {
        super.onPause()
        textToSpeech.stop()
    }

    override fun onStop() {
        super.onStop()

        // Set status bar back to light if not in night mode.
        if (!AppUtils.isDarkMode(this)) {
            AppUtils.setStatusBarLight(this)
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }

    override fun onBackPressed() {
        finish()
    }

    private fun removeFragments() {
        // Remove flashcard fragments.
        for (fragment in supportFragmentManager.fragments) {
            supportFragmentManager.beginTransaction().remove(fragment).commitNow()
        }
    }

    private fun createFrontCardFragment(dictionaryEntry: DictionaryEntry) {
        // Create flashcard fragment.
        val cardFragment = FlashCardFragment(dictionaryEntry, FlashCardFragment.Type.Front) { action, direction -> handleCardAction(action, direction) }
        supportFragmentManager.beginTransaction().add(R.id.fragment_container, cardFragment).commit()

        // Animate flash card.
        cardFragment.lifecycleScope.launchWhenResumed {
            val xy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
            val cardView = cardFragment.getCardView()
            cardView.translationX = xy
            cardView.translationY = xy
            cardView.animate().setDuration(100).translationX(-xy).translationY(-xy).start()
        }
    }

    private fun createBackCardFragment(dictionaryEntry: DictionaryEntry) {
        val cardFragment = FlashCardFragment(dictionaryEntry, FlashCardFragment.Type.Back) { action, direction -> handleCardAction(action, direction) }
        supportFragmentManager
                .beginTransaction()
                .setCustomAnimations(R.animator.card_flip_left_in, R.animator.card_flip_left_out, R.animator.card_flip_left_in, R.animator.card_flip_left_out)
                .replace(R.id.fragment_container, cardFragment)
                .addToBackStack(null)
                .commit()

        if (!soundMuted) {
            cardFragment.lifecycleScope.launchWhenResumed {
                textToSpeech.speak(dictionaryEntry.reading[0].kana, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun updateProgress(text: String, progress: Int) {
        // Update progress.
        progressText.text = text
        progressBar.setProgressCompat(progress, true)
    }

    private fun updateRestartButton(size: Int) {
        if (size > 0) {
            restartButton.alpha = 0.5f
            restartButton.isEnabled = false
        }
        else {
            restartButton.alpha = 1f
            restartButton.isEnabled = true
        }
    }

    private fun updateUndoButton(size: Int) {
        if (size > 0) {
            undoButton.alpha = 1f
            undoButton.isEnabled = true
        }
        else {
            undoButton.alpha = 0.5f
            undoButton.isEnabled = false
        }
    }

    private fun updateBackgroundCard(visible: Boolean, text: String) {
        val visibility = when (visible) {
            true -> View.VISIBLE
            false -> View.INVISIBLE
        }
        backgroundCard.visibility = visibility
        backgroundCardText.text = text
    }

    private fun toggleSound(button: ImageButton) {
        soundMuted = !soundMuted
        if (!soundMuted)
            button.setImageResource(R.drawable.ic_volume)
        else
            button.setImageResource(R.drawable.ic_volume_off)
    }

    private fun handleCardAction(action: FlashCardFragment.Action, direction: Int) {
        when (action) {
            FlashCardFragment.Action.Click -> {
                flipCard()
            }
            FlashCardFragment.Action.Dismiss -> {
                when (direction) {
                    DraggableCardView.DIRECTION_LEFT -> {
                        viewModel.redoCard()
                    }
                    DraggableCardView.DIRECTION_RIGHT -> {
                        viewModel.dismissCard()
                    }
                }
                drawCard()
            }
        }
    }

    private fun drawCard() {
        this.removeFragments()

        // Get cards from view model.
        val flashCards = viewModel.getFlashCards()
        val flashCardsDone = viewModel.getFlashCardsDone()

        val progressText = String.format("%d / %d", flashCardsDone.size, flashCardsDone.size + flashCards.size)
        val progress = flashCardsDone.size.toFloat() / (flashCardsDone.size + flashCards.size).toFloat() * 100f
        updateProgress(progressText, progress.toInt())
        updateRestartButton(flashCards.size)
        updateUndoButton(flashCardsDone.size)

        if (flashCards.isEmpty())
            return

        if (flashCards.size > 1)
            updateBackgroundCard(true, flashCards[1].getReading())
        else
            updateBackgroundCard(false, "")

        createFrontCardFragment(flashCards[0])
        viewModel.canFlip = true
    }

    private fun flipCard() {
        if (!viewModel.canFlip)
            return

        // Get cards from view model.
        val flashCards = viewModel.getFlashCards()
        createBackCardFragment(flashCards[0])
        viewModel.canFlip = false
    }
}