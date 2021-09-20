package com.andlill.jld.app.dictionarydetails

import android.content.Intent
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.dictionarydetails.adapter.KanjiAdapter
import com.andlill.jld.app.dictionarydetails.adapter.ReadingAdapter
import com.andlill.jld.app.dictionarydetails.adapter.TranslationAdapter
import com.andlill.jld.app.dictionarydetails.dialog.AddToCollectionDialog
import com.andlill.jld.model.DictionaryEntry
import java.util.*

class DictionaryDetailsActivity : AppCompatActivity() {

    companion object {
        const val ARGUMENT_ENTRY_ID = "com.andlill.jld.DictionaryEntry"
        const val ARGUMENT_CALLED_FROM_EXTERNAL = "com.andlill.jld.CalledFromCollection"
        const val RESULT_ENTRY_ID = "com.andlill.jld.ResultEntryId"
    }

    private lateinit var viewModel: DictionaryDetailsViewModel

    private lateinit var textToSpeech: TextToSpeech
    private var menuItemCollection: MenuItem? = null
    private var calledFromExternal = false
    private var existsInCollection = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dictionary_details)

        // Get dictionary entry from intent arguments.
        val entryId = intent.getIntExtra(ARGUMENT_ENTRY_ID, 0)
        calledFromExternal = intent.getBooleanExtra(ARGUMENT_CALLED_FROM_EXTERNAL, false)

        viewModel = ViewModelProvider(this).get(DictionaryDetailsViewModel::class.java)
        viewModel.initialize(this, entryId)

        val entry = viewModel.getDictionaryEntry().value as DictionaryEntry

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        viewModel.getCollections().observe(this, { collections ->
            existsInCollection = false
            collections.forEach { collection ->
                if (collection.content.contains(entry.id)) {
                    existsInCollection = true
                    return@forEach
                }
            }
            updateMenuItemCollection()
        })

        // Set top primary reading.
        if (entry.reading[0].kanji.isNotEmpty()) {
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
        if (entry.reading[0].kanji.isNotEmpty()) {
            findViewById<RecyclerView>(R.id.recycler_kanji).apply {
                layoutManager = LinearLayoutManager(this@DictionaryDetailsActivity)
                addItemDecoration(DividerItemDecoration(this@DictionaryDetailsActivity, DividerItemDecoration.VERTICAL))
                adapter = KanjiAdapter(viewModel.getKanji(entry.reading[0].kanji))
            }
        }
        else {
            // Hide kanji if none exists.
            findViewById<View>(R.id.layout_kanji).visibility = View.GONE
        }

        // Recycler view for alternative readings (skip reading zero, as it is primary reading).
        if (entry.reading.size > 1) {
            findViewById<RecyclerView>(R.id.recycler_reading).apply {
                layoutManager = LinearLayoutManager(this@DictionaryDetailsActivity)
                addItemDecoration(DividerItemDecoration(this@DictionaryDetailsActivity, DividerItemDecoration.VERTICAL))
                adapter = ReadingAdapter(entry.reading.subList(1, entry.reading.size))
            }
        }
        else {
            // Hide alternative readings if none exists.
            findViewById<View>(R.id.layout_alternative_reading).visibility = View.GONE
        }
    }

    override fun onResume() {
        super.onResume()
        textToSpeech = TextToSpeech(this) { status ->
            if (status == TextToSpeech.SUCCESS) {
                textToSpeech.language = Locale.JAPANESE
            }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_delete -> removeFromCollection()
            R.id.menu_item_collection_add -> AddToCollectionDialog(viewModel).show(supportFragmentManager, AddToCollectionDialog::class.simpleName)
            android.R.id.home -> finish()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        when (calledFromExternal) {
            true -> {
                menuInflater.inflate(R.menu.menu_activity_dictionary_details_external, menu)
            }
            false -> {
                menuInflater.inflate(R.menu.menu_activity_dictionary_details, menu)
                menuItemCollection = menu?.findItem(R.id.menu_item_collection_add) as MenuItem
                updateMenuItemCollection()
            }
        }
        return true
    }

    private fun updateMenuItemCollection() {
        if (existsInCollection) {
            menuItemCollection?.icon = ContextCompat.getDrawable(this, R.drawable.ic_collection_check)
        } else {
            menuItemCollection?.icon = ContextCompat.getDrawable(this, R.drawable.ic_collection_add)
        }
    }

    private fun removeFromCollection() {
        val entry = viewModel.getDictionaryEntry().value as DictionaryEntry
        val intent = Intent()
        intent.putExtra(RESULT_ENTRY_ID, entry.id)
        setResult(RESULT_OK, intent)
        finish()
    }
}