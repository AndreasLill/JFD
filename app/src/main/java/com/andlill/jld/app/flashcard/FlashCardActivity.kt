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
import com.andlill.jld.utils.AppSettings
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
    private lateinit var restartButton: View
    private lateinit var nextStageButton: View
    private lateinit var restartText: TextView
    private lateinit var nextStageText: TextView
    private lateinit var stageText: TextView
    private lateinit var progressText: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var backgroundCard: View
    private lateinit var backgroundCardText: TextView
    private lateinit var textToSpeech: TextToSpeech

    private var textToSpeechEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        // Get settings.
        textToSpeechEnabled = AppSettings.getTextToSpeech(this)

        // Get intent extras.
        val collection = intent.getSerializableExtra(ARGUMENT_COLLECTION) as Collection

        // Setup flashcard view model.
        viewModel = ViewModelProvider(this).get(FlashCardViewModel::class.java)
        viewModel.initialize(collection)

        // Setup views.
        stageText = findViewById(R.id.text_stage)
        progressText = findViewById(R.id.text_progress)
        progressBar = findViewById(R.id.progress_bar)
        backgroundCard = findViewById(R.id.card_background)
        backgroundCardText = findViewById(R.id.text_background)
        restartText = findViewById(R.id.text_restart)
        nextStageText = findViewById(R.id.text_next_stage)
        restartButton = findViewById<View>(R.id.button_restart).apply { setOnClickListener { restart(collection) } }
        nextStageButton = findViewById<View>(R.id.button_next_stage).apply { setOnClickListener { nextStage() } }
        findViewById<ImageButton>(R.id.button_action_restart).apply { setOnClickListener { restart(collection) } }
        findViewById<ImageButton>(R.id.button_action_back).apply { setOnClickListener { finish() } }

        viewModel.getFlashCards().observe(this, { cards ->
            updateProgress()
            updateRestartButton(cards.size, viewModel.getStageSize())
            updateNextStageButton(cards.size, viewModel.getStageSize())
        })
        viewModel.getStage().observe(this, { stage ->
            stageText.text = String.format(getString(R.string.study_stage), stage)
        })

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

        if (textToSpeechEnabled) {
            cardFragment.lifecycleScope.launchWhenResumed {
                textToSpeech.speak(dictionaryEntry.reading[0].kana, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }
    }

    private fun updateProgress() {
        progressText.text = viewModel.getProgressText()
        progressBar.setProgressCompat(viewModel.getProgressAmount(), true)
    }

    private fun updateRestartButton(size: Int, stageSize: Int) {
        if(size == 0 && stageSize == 0) {
            restartText.animate().setDuration(100).alpha(1f).start()
            restartButton.animate().setDuration(100).alpha(1f).withStartAction {
                restartButton.isEnabled = true
                restartButton.visibility = View.VISIBLE
            }.start()
        }
        else {
            restartText.animate().setDuration(100).alpha(0f).start()
            restartButton.animate().setDuration(100).alpha(0f).withEndAction {
                restartButton.isEnabled = false
                restartButton.visibility = View.INVISIBLE
            }.start()
        }
    }

    private fun updateNextStageButton(size: Int, stageSize: Int) {
        if(size == 0 && stageSize > 0) {
            nextStageText.animate().setDuration(100).alpha(1f).start()
            nextStageButton.animate().setDuration(100).alpha(1f).withStartAction {
                nextStageButton.isEnabled = true
                nextStageButton.visibility = View.VISIBLE
            }.start()
        }
        else {
            nextStageText.animate().setDuration(100).alpha(0f).start()
            nextStageButton.animate().setDuration(100).alpha(0f).withEndAction {
                nextStageButton.isEnabled = false
                nextStageButton.visibility = View.INVISIBLE
            }.start()
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

    private fun handleCardAction(action: FlashCardFragment.Action, direction: Int) {
        when (action) {
            FlashCardFragment.Action.Click -> {
                flipCard()
            }
            FlashCardFragment.Action.Dismiss -> {
                when (direction) {
                    DraggableCardView.DIRECTION_LEFT -> {
                        viewModel.stageCard()
                    }
                    DraggableCardView.DIRECTION_RIGHT -> {
                        viewModel.dismissCard()
                    }
                }
                drawCard()
            }
        }
    }

    private fun restart(collection: Collection) {
        viewModel.initialize(collection)
        drawCard()
    }

    private fun nextStage() {
        viewModel.nextStage()
        drawCard()
    }

    private fun drawCard() {
        this.removeFragments()

        val flashCards = viewModel.getFlashCards().value as ArrayList
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

        val flashCards = viewModel.getFlashCards().value as ArrayList
        createBackCardFragment(flashCards[0])
        viewModel.canFlip = false
    }
}