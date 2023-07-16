package com.andlill.jfd.app.dictionarydetails

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.dictionarydetails.adapter.KanjiAdapter
import com.andlill.jfd.app.dictionarydetails.adapter.TranslationAdapter
import com.andlill.jfd.app.dictionarydetails.dialog.AddToCollectionDialog
import com.andlill.jfd.utils.AppSettings
import com.andlill.jfd.utils.AppUtils
import java.util.*

class DictionaryDetailsActivity : AppCompatActivity() {

    companion object {
        const val ARGUMENT_ENTRY_ID = "com.andlill.jld.DictionaryEntry"
        const val ARGUMENT_CALLED_FROM_EXTERNAL = "com.andlill.jld.CalledFromCollection"
    }

    private lateinit var viewModel: DictionaryDetailsActivityViewModel
    private lateinit var textToSpeech: TextToSpeech

    private var textToSpeechEnabled = true
    private var calledFromExternal = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_details)

        // Get settings.
        textToSpeechEnabled = AppSettings.getTextToSpeech(this)

        // Get dictionary entry from intent arguments.
        val entryId = intent.getIntExtra(ARGUMENT_ENTRY_ID, 0)
        calledFromExternal = intent.getBooleanExtra(ARGUMENT_CALLED_FROM_EXTERNAL, false)

        viewModel = ViewModelProvider(this)[DictionaryDetailsActivityViewModel::class.java]
        viewModel.initialize(this, entryId)

        val entry = viewModel.getDictionaryEntry()

        findViewById<View>(R.id.button_add_to_collection).apply {
            // Hide if called from external collection.
            if (calledFromExternal)
                visibility = View.GONE

            setOnClickListener { AddToCollectionDialog(entry.id).show(supportFragmentManager, AddToCollectionDialog::class.simpleName) }
        }
        findViewById<View>(R.id.button_back).setOnClickListener { finish() }
        findViewById<View>(R.id.button_text_to_speech).setOnClickListener {
            if (textToSpeechEnabled) {
                textToSpeech.speak(entry.reading[0].kana, TextToSpeech.QUEUE_FLUSH, null, null)
            }
        }

        // Set top primary reading.
        if (!entry.reading[0].kanji.isNullOrEmpty()) {
            findViewById<TextView>(R.id.text_primary_kanji).text = entry.reading[0].kanji
            findViewById<TextView>(R.id.text_primary_kana).text = entry.reading[0].kana
        }
        else {
            findViewById<TextView>(R.id.text_primary_kanji).text = entry.reading[0].kana
            findViewById<TextView>(R.id.text_primary_kana).visibility = View.GONE
        }

        // Recycler view for English translations.
        findViewById<RecyclerView>(R.id.recycler_translation).apply {
            layoutManager = LinearLayoutManager(this@DictionaryDetailsActivity)
            adapter = TranslationAdapter(entry.sense)
        }

        // Recycler view for Kanji in word.
        entry.reading[0].kanji?.let {
            findViewById<View>(R.id.layout_kanji).visibility = View.VISIBLE
            findViewById<RecyclerView>(R.id.recycler_kanji).apply {
                layoutManager = LinearLayoutManager(this@DictionaryDetailsActivity)
                adapter = KanjiAdapter(viewModel.getKanji(this@DictionaryDetailsActivity, it))
            }
        }

        // Verb conjugations
        if (viewModel.getVerbConjugation().affirmative.present.isNotEmpty()) {
            findViewById<View>(R.id.layout_verb_conjugation).visibility = View.VISIBLE
            findViewById<TextView>(R.id.text_verb_affirmative_present).text = viewModel.getVerbConjugation().affirmative.present
            findViewById<TextView>(R.id.text_verb_negative_present).text = viewModel.getVerbConjugation().negative.present
            findViewById<TextView>(R.id.text_verb_affirmative_past).text = viewModel.getVerbConjugation().affirmative.past
            findViewById<TextView>(R.id.text_verb_negative_past).text = viewModel.getVerbConjugation().negative.past
            findViewById<TextView>(R.id.text_verb_affirmative_te_form).text = viewModel.getVerbConjugation().affirmative.teForm
            findViewById<TextView>(R.id.text_verb_negative_te_form).text = viewModel.getVerbConjugation().negative.teForm
            findViewById<TextView>(R.id.text_verb_affirmative_potential).text = viewModel.getVerbConjugation().affirmative.potential
            findViewById<TextView>(R.id.text_verb_negative_potential).text = viewModel.getVerbConjugation().negative.potential
            findViewById<TextView>(R.id.text_verb_affirmative_passive).text = viewModel.getVerbConjugation().affirmative.passive
            findViewById<TextView>(R.id.text_verb_negative_passive).text = viewModel.getVerbConjugation().negative.passive
            findViewById<TextView>(R.id.text_verb_affirmative_causative).text = viewModel.getVerbConjugation().affirmative.causative
            findViewById<TextView>(R.id.text_verb_negative_causative).text = viewModel.getVerbConjugation().negative.causative
            findViewById<TextView>(R.id.text_verb_affirmative_imperative).text = viewModel.getVerbConjugation().affirmative.imperative
            findViewById<TextView>(R.id.text_verb_negative_imperative).text = viewModel.getVerbConjugation().negative.imperative
        }
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

    override fun onStop() {
        super.onStop()

        // Set status bar back to light if not in night mode.
        if (!AppUtils.isDarkMode(this)) {
            AppUtils.setStatusBarLight(this)
        }
    }

    override fun onPause() {
        super.onPause()
        textToSpeech.stop()
    }

    override fun onDestroy() {
        super.onDestroy()
        textToSpeech.shutdown()
    }
}