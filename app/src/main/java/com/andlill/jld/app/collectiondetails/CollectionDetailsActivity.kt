package com.andlill.jld.app.collectiondetails

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.collectiondetails.adapter.CollectionContentAdapter
import com.andlill.jld.app.dictionarydetails.DictionaryDetailsActivity
import com.andlill.jld.app.flashcard.FlashCardActivity
import com.andlill.jld.app.shared.dialog.ConfirmationDialog
import com.andlill.jld.app.shared.dialog.RenameCollectionDialog
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.util.*
import kotlin.collections.HashMap

class CollectionDetailsActivity : AppCompatActivity() {

    companion object {
        const val ARGUMENT_COLLECTION_ID = "com.andlill.jld.CollectionId"
    }

    enum class ActivityState {
        Default,
        Selection
    }

    private lateinit var viewModel: CollectionDetailsViewModel

    private lateinit var activityMenu: Menu
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var contentAdapter: CollectionContentAdapter
    private lateinit var buttonStudy: View
    private var state = ActivityState.Default

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_details)

        // Get intent extras.
        val collectionId = intent.getLongExtra(ARGUMENT_COLLECTION_ID, 0)
        viewModel = ViewModelProvider(this).get(CollectionDetailsViewModel::class.java)
        viewModel.initialize(this, collectionId)

        // Setup toolbar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        contentAdapter = CollectionContentAdapter(viewModel) { action, entry ->
            when (action) {
                CollectionContentAdapter.Action.Select -> startActivityDictionaryDetails(entry)
                CollectionContentAdapter.Action.SelectionStart -> startSelectionMode()
                CollectionContentAdapter.Action.SelectionUpdate -> updateSelectionMode()
                CollectionContentAdapter.Action.SelectionEnd -> endSelectionMode()
            }
        }

        contentRecyclerView = findViewById<RecyclerView>(R.id.recycler_collection_content).apply {
            layoutManager = LinearLayoutManager(this@CollectionDetailsActivity)
            addItemDecoration(DividerItemDecoration(this@CollectionDetailsActivity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            this.adapter = contentAdapter
        }

        viewModel.getCollection().observe(this, {
            updateToolBarTitle()
        })

        buttonStudy = findViewById<View>(R.id.button_study).apply { setOnClickListener { study() } }
    }

    override fun onBackPressed() {
        when (state) {
            ActivityState.Default -> finish()
            ActivityState.Selection -> endSelectionMode()
        }
    }

    private fun startActivityDictionaryDetails(entry: DictionaryEntry) {
        // Cancel selection before starting new activity.
        endSelectionMode()

        val intent = Intent(this, DictionaryDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(DictionaryDetailsActivity.ARGUMENT_ENTRY_ID, entry.id)
        intent.putExtra(DictionaryDetailsActivity.ARGUMENT_CALLED_FROM_EXTERNAL, true)
        startActivity(intent)
    }

    private fun study() {
        val collection = viewModel.getCollection().value as Collection
        val intent = Intent(this, FlashCardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(FlashCardActivity.ARGUMENT_COLLECTION, collection)
        startActivity(intent)
    }

    private fun share() {
        val collection = viewModel.getCollection().value as Collection
        val json = Gson().toJson(collection)
        val base64 = Base64.encodeToString(json.toByteArray(), Base64.NO_WRAP)
        val share = Intent.createChooser(Intent().apply {
            action = Intent.ACTION_SEND
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, base64)
            putExtra(Intent.EXTRA_TITLE, getString(R.string.collection_share_title))
        }, null)
        startActivity(share)
    }

    private fun rename() {
        val collection = viewModel.getCollection().value as Collection
        RenameCollectionDialog(collection.name) { name ->
            viewModel.renameCollection(this, name)
        }.show(supportFragmentManager, RenameCollectionDialog::class.simpleName)
    }

    private fun delete() {
        val collection = viewModel.getCollection().value as Collection
        ConfirmationDialog(getString(R.string.dialog_confirm_collection_delete_body)) {
            val intent = Intent()
            intent.putExtra(ARGUMENT_COLLECTION_ID, collection.id)
            setResult(RESULT_OK, intent)
            finish()
        }.show(supportFragmentManager, ConfirmationDialog::class.simpleName)
    }

    private fun startSelectionMode() {
        if (state == ActivityState.Selection)
            return

        state = ActivityState.Selection
        activityMenu.setGroupVisible(R.id.group_default, false)
        activityMenu.setGroupVisible(R.id.group_selection, true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        buttonStudy.animate().alpha(0f)
    }

    private fun updateSelectionMode() {
        val selection = contentAdapter.getSelection()
        supportActionBar?.title = getString(R.string.collection_details_selection_title)
        supportActionBar?.subtitle = String.format(getString(R.string.item_count, selection.size))
    }

    private fun endSelectionMode() {
        if (state == ActivityState.Default)
            return

        contentAdapter.clearSelection()
        state = ActivityState.Default
        activityMenu.setGroupVisible(R.id.group_default, true)
        activityMenu.setGroupVisible(R.id.group_selection, false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        buttonStudy.animate().alpha(1f)
        updateToolBarTitle()
    }

    private fun deleteSelection() {
        val selection = contentAdapter.getSelection()
        // Save data to a HashMap with index as key to preserve the index.
        val selectionMap = HashMap<Int, Int>()
        selection.forEach {
            val index = contentAdapter.indexOf(it)
            selectionMap[index] = it
        }
        removeContent(selectionMap)
        endSelectionMode()
    }

    private fun updateToolBarTitle() {
        val collection = viewModel.getCollection().value as Collection
        supportActionBar?.title = collection.name
        supportActionBar?.subtitle = if (collection.content.isNotEmpty()) { String.format(getString(R.string.item_count), collection.content.size) } else { getString(R.string.empty) }
    }

    private fun removeContent(selection: HashMap<Int, Int>) {
        // Get the lowest index to use for range change.
        val lowestIndex = selection.keys.minOrNull() as Int
        // Remove content from collection and callback.
        viewModel.removeContent(this, selection) {
            // Remove the content by key in selection.
            selection.keys.forEach { index -> contentAdapter.notifyItemRemoved(index) }
            // Update item range using the lowest index.
            contentAdapter.notifyItemRangeChanged(lowestIndex, contentAdapter.itemCount)
            Snackbar.make(findViewById(R.id.layout_root), String.format(getString(R.string.collection_details_content_removed), selection.size), 6000).setAction(getString(R.string.undo)) {
                // Undo the remove action and add the content back to the collection.
                viewModel.addContent(this, selection) {
                    // Restore the content by index/value pair.
                    selection.keys.forEach { index -> contentAdapter.notifyItemInserted(index) }
                    // Update item range using the lowest index.
                    contentAdapter.notifyItemRangeChanged(lowestIndex, contentAdapter.itemCount)
                    updateToolBarTitle()
                }
            }.show()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_share -> share()
            R.id.menu_item_rename -> rename()
            R.id.menu_item_delete -> delete()
            R.id.menu_item_selection_delete -> deleteSelection()
            android.R.id.home -> {
                when (state) {
                    ActivityState.Default -> finish()
                    ActivityState.Selection -> endSelectionMode()
                }
            }
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_collection_details, menu)
        this.activityMenu = menu as Menu
        MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }
}