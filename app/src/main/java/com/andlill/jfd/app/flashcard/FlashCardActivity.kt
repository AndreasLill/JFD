package com.andlill.jfd.app.flashcard

import android.content.Context
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.util.TypedValue
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.flashcard.adapter.FlashcardsSummaryAdapter
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import com.andlill.jfd.utils.AppSettings
import com.andlill.jfd.utils.AppUtils
import com.google.android.material.progressindicator.LinearProgressIndicator
import kotlinx.coroutines.launch
import java.util.*

class FlashCardActivity : AppCompatActivity() {

    companion object {
        const val ARGUMENT_COLLECTION = "com.andlill.jld.Collection"
    }

    private lateinit var viewModel: FlashCardActivityViewModel
    private lateinit var restartButton: View
    private lateinit var progressText: TextView
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var backgroundCard: View
    private lateinit var backgroundCardText: TextView
    private lateinit var textToSpeech: TextToSpeech
    private lateinit var recyclerSummary: RecyclerView
    private lateinit var summaryAdapter: FlashcardsSummaryAdapter

    private var textToSpeechEnabled = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flashcard)

        // Get settings.
        textToSpeechEnabled = AppSettings.getTextToSpeech(this)

        // Get intent extras.
        val collection = intent.getSerializableExtra(ARGUMENT_COLLECTION) as Collection

        // Setup flashcard view model.
        viewModel = ViewModelProvider(this)[FlashCardActivityViewModel::class.java]
        viewModel.initialize(this, collection)

        // Setup views.
        progressText = findViewById(R.id.text_progress)
        progressBar = findViewById(R.id.progress_bar)
        backgroundCard = findViewById(R.id.card_background)
        backgroundCardText = findViewById(R.id.text_background)
        restartButton = findViewById<View>(R.id.button_restart).apply { setOnClickListener { restart(this@FlashCardActivity, collection) } }
        findViewById<ImageButton>(R.id.button_back).apply { setOnClickListener { finish() } }

        summaryAdapter = FlashcardsSummaryAdapter()
        recyclerSummary = findViewById<RecyclerView>(R.id.recycler_flashcards_summary).apply {
            layoutManager = LinearLayoutManager(this@FlashCardActivity)
            adapter = summaryAdapter
            itemAnimator = null
        }

        viewModel.getSummary().observe(this) { items ->
            summaryAdapter.submitList(items.toList())
        }
        viewModel.getFlashCards().observe(this) { cards ->
            updateProgress(cards.size)
        }

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

    @Deprecated("Deprecated in Java", ReplaceWith("finish()"))
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
        cardFragment.lifecycleScope.launch {
            cardFragment.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                val xy = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2f, resources.displayMetrics)
                val cardView = cardFragment.getCardView()
                cardView.translationX = xy
                cardView.translationY = xy
                cardView.animate().setDuration(100).translationX(-xy).translationY(-xy).start()
            }
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
            cardFragment.lifecycleScope.launch {
                cardFragment.lifecycle.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    textToSpeech.speak(dictionaryEntry.reading[0].kana, TextToSpeech.QUEUE_FLUSH, null, null)
                }
            }
        }
    }

    private fun updateProgress(size: Int) {
        progressText.text = viewModel.getProgressText()
        progressBar.setProgressCompat(viewModel.getProgressAmount(), true)

        if(size == 0) {
            restartButton.animate().setDuration(100).alpha(1f).withStartAction {
                restartButton.isEnabled = true
                restartButton.visibility = View.VISIBLE
            }.start()
            recyclerSummary.animate().setDuration(100).alpha(1f).withStartAction {
                recyclerSummary.visibility = View.VISIBLE
            }
        }
        else {
            restartButton.animate().setDuration(100).alpha(0f).withEndAction {
                restartButton.isEnabled = false
                restartButton.visibility = View.INVISIBLE
            }.start()
            recyclerSummary.animate().setDuration(100).alpha(1f).withStartAction {
                recyclerSummary.visibility = View.INVISIBLE
            }
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

    private fun restart(context: Context, collection: Collection) {
        viewModel.initialize(context, collection)
        drawCard()
    }

    private fun drawCard() {
        this.removeFragments()

        val flashCards = viewModel.getFlashCards().value as ArrayList
        if (flashCards.isEmpty())
            return

        if (flashCards.size > 1)
            updateBackgroundCard(true, flashCards[1].primaryReading)
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