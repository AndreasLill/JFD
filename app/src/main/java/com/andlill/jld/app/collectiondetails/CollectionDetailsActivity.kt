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
import com.google.gson.Gson
import java.util.*

class CollectionDetailsActivity : AppCompatActivity() {

    companion object {
        const val ARGUMENT_COLLECTION_ID = "com.andlill.jld.CollectionId"
    }

    enum class ActivityState {
        Default,
        Selection
    }

    // ViewModel
    private lateinit var viewModel: CollectionDetailsViewModel

    // Views
    private lateinit var activityMenu: Menu
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var contentAdapter: CollectionContentAdapter
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
                CollectionContentAdapter.Action.Selection -> updateSelection()
            }
        }

        contentRecyclerView = findViewById<RecyclerView>(R.id.recycler_collection_content).apply {
            layoutManager = LinearLayoutManager(this@CollectionDetailsActivity)
            addItemDecoration(DividerItemDecoration(this@CollectionDetailsActivity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            this.adapter = contentAdapter
        }

        viewModel.getCollection().observe(this, { collection ->
            contentAdapter.clearSelection()
            contentAdapter.submitList(collection.content.toList())
            updateToolBarTitle()
        })

        findViewById<View>(R.id.button_study).apply { setOnClickListener { study() } }
    }

    override fun onBackPressed() {
        when (state) {
            ActivityState.Default -> finish()
            ActivityState.Selection -> cancelSelection()
        }
    }

    private fun startActivityDictionaryDetails(entry: DictionaryEntry) {
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
            putExtra(Intent.EXTRA_TITLE, getString(R.string.collection_details_share_title))
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

    private fun updateSelection() {
        val selection = contentAdapter.getSelection()

        if (selection.isEmpty()) {
            endSelection()
            return
        }

        state = ActivityState.Selection
        activityMenu.setGroupVisible(R.id.group_default, false)
        activityMenu.setGroupVisible(R.id.group_selection, true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
        supportActionBar?.title = "Selection"
        supportActionBar?.subtitle = String.format("${selection.size} items selected")
    }

    private fun endSelection() {
        state = ActivityState.Default
        activityMenu.setGroupVisible(R.id.group_default, true)
        activityMenu.setGroupVisible(R.id.group_selection, false)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_arrow_back)
        updateToolBarTitle()
    }

    private fun cancelSelection() {
        contentAdapter.cancelSelection()
        endSelection()
    }

    private fun deleteSelection() {
        val selection = contentAdapter.getSelection()
        viewModel.removeContent(this, selection)
        endSelection()
    }

    private fun updateToolBarTitle() {
        val collection = viewModel.getCollection().value as Collection
        supportActionBar?.title = collection.name
        supportActionBar?.subtitle = if (collection.content.isNotEmpty()) { String.format(getString(R.string.items), collection.content.size) } else { getString(R.string.empty) }
    }

    /*
    private fun removeContent(id: Int, index: Int) {
        val entry = viewModel.getDictionaryEntry(id)
        viewModel.removeContent(this, index) {
            contentAdapter.notifyItemRemoved(index)
            contentAdapter.notifyItemRangeChanged(index, contentAdapter.itemCount)
            // Show Undo snackbar.
            Snackbar.make(findViewById(R.id.layout_root), String.format(getString(R.string.collection_details_delete_entry), entry.getReading()), 6000).setAction(getString(R.string.undo)) {
                // Undo the delete action.
                viewModel.addContent(this, index, id) {
                    contentAdapter.notifyItemInserted(index)
                    contentAdapter.notifyItemRangeChanged(index, contentAdapter.itemCount)
                }
            }.show()
        }
    }
     */

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_share -> share()
            R.id.menu_item_rename -> rename()
            R.id.menu_item_delete -> delete()
            R.id.menu_item_selection_delete -> deleteSelection()
            android.R.id.home -> {
                when (state) {
                    ActivityState.Default -> finish()
                    ActivityState.Selection -> cancelSelection()
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