package com.andlill.jld.app.collectiondetails

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.appcompat.widget.Toolbar
import androidx.core.view.MenuCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.shared.ResultActivity
import com.andlill.jld.app.collectiondetails.adapter.CollectionContentAdapter
import com.andlill.jld.app.collectiondetails.dialog.RenameCollectionDialog
import com.andlill.jld.app.dictionarydetails.DictionaryDetailsActivity
import com.andlill.jld.app.flashcard.FlashCardActivity
import com.andlill.jld.app.shared.dialog.ConfirmationDialog
import com.andlill.jld.app.shared.dialog.DialogResult
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import java.util.*

class CollectionDetailsActivity : ResultActivity() {

    companion object {
        const val ARGUMENT_COLLECTION_ID = "com.andlill.jld.app.collection.CollectionDetailsActivity.COLLECTION_ID"
    }

    // ViewModel
    private lateinit var viewModel: CollectionDetailsViewModel

    // Views
    private lateinit var contentRecyclerView: RecyclerView
    private lateinit var contentAdapter: CollectionContentAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_collection_details)

        // Get intent extras.
        val collectionId = intent.getLongExtra(ARGUMENT_COLLECTION_ID, 0)
        viewModel = ViewModelProvider(this).get(CollectionDetailsViewModel::class.java)
        viewModel.initialize(this, collectionId)

        val collection = viewModel.getCollection().value as Collection

        // Setup toolbar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = collection.name
        supportActionBar?.subtitle = if (collection.content.isNotEmpty()) { String.format(getString(R.string.items), collection.content.size) } else { getString(R.string.empty) }

        contentAdapter = CollectionContentAdapter(viewModel) { entry ->
            startActivityDictionaryDetails(entry)
        }

        contentRecyclerView = findViewById<RecyclerView>(R.id.recycler_collection_content).apply {
            layoutManager = LinearLayoutManager(this@CollectionDetailsActivity)
            addItemDecoration(DividerItemDecoration(this@CollectionDetailsActivity, DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            this.adapter = contentAdapter
        }

        /*
        CollectionTouchAdapter(this, contentAdapter, collection, ItemTouchHelper.UP or ItemTouchHelper.DOWN, 0) {
            collectionViewModel.updateCollection(this@CollectionDetailsActivity, collection)
        }.attachToRecyclerView(contentRecyclerView)
        */

        viewModel.getCollection().observe(this, {
            supportActionBar?.title = it.name
            supportActionBar?.subtitle = if (it.content.isNotEmpty()) { String.format(getString(R.string.items), it.content.size) } else { getString(R.string.empty) }
        })

        findViewById<View>(R.id.button_study).apply { setOnClickListener { study() } }
    }

    override fun onBackPressed() {
        finish()
    }

    private fun startActivityDictionaryDetails(entry: DictionaryEntry) {
        val intent = Intent(this, DictionaryDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(DictionaryDetailsActivity.ARGUMENT_ENTRY_ID, entry.id)
        intent.putExtra(DictionaryDetailsActivity.ARGUMENT_CALLED_FROM_COLLECTION, true)
        activityLauncher.launch(intent)
    }

    private fun study() {
        val collection = viewModel.getCollection().value as Collection
        val intent = Intent(this, FlashCardActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(FlashCardActivity.ARGUMENT_COLLECTION, collection)
        activityLauncher.launch(intent)
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
        RenameCollectionDialog(collection.name) { result, name ->
            when (result) {
                DialogResult.OK -> {
                    viewModel.renameCollection(this@CollectionDetailsActivity, name)
                }
                DialogResult.Dismiss -> {}
            }
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_share -> share()
            R.id.menu_item_rename -> rename()
            R.id.menu_item_delete -> delete()
            android.R.id.home -> finish()
        }

        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_collection_details, menu)
        MenuCompat.setGroupDividerEnabled(menu, true)
        return true
    }

    override fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            if (result.data?.hasExtra(DictionaryDetailsActivity.RESULT_ENTRY_ID) == true) {
                val id = result.data?.getIntExtra(DictionaryDetailsActivity.RESULT_ENTRY_ID, 0) as Int
                val entry = viewModel.getDictionaryEntry(id)
                val index = contentAdapter.getIndexOf(id)

                viewModel.removeContent(this@CollectionDetailsActivity, index) {
                    contentAdapter.notifyItemRemoved(index)
                    contentAdapter.notifyItemRangeChanged(index, contentAdapter.itemCount)
                    // Show Undo snackbar.
                    Snackbar.make(findViewById(android.R.id.content), String.format(getString(R.string.collection_details_delete_entry), entry.getReading()), 6000).setAction(getString(R.string.undo)) {
                        // Undo the delete action.
                        viewModel.addContent(this@CollectionDetailsActivity, index, id) {
                            contentAdapter.notifyItemInserted(index)
                            contentAdapter.notifyItemRangeChanged(index, contentAdapter.itemCount)
                        }
                    }.show()
                }
            }
        }
    }
}