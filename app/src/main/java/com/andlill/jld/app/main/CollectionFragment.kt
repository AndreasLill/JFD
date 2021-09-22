package com.andlill.jld.app.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.core.os.HandlerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.collectiondetails.CollectionDetailsActivity
import com.andlill.jld.app.main.adapter.CollectionAdapter
import com.andlill.jld.app.main.dialog.ImportCollectionDialog
import com.andlill.jld.app.main.dialog.NameCollectionDialog
import com.andlill.jld.app.shared.ResultActivityFragment
import com.andlill.jld.app.shared.dialog.RenameCollectionDialog
import com.andlill.jld.model.Collection
import com.andlill.jld.utils.ViewAnimation
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar

class CollectionFragment : ResultActivityFragment(R.layout.fragment_collection) {

    private lateinit var viewModel: CollectionFragmentViewModel
    private lateinit var collectionAdapter: CollectionAdapter

    private lateinit var scrim: View
    private lateinit var actionButton: View
    private lateinit var actionButtonNew: View
    private lateinit var actionButtonImport: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CollectionFragmentViewModel::class.java)

        collectionAdapter = CollectionAdapter { action, collection->
            when (action) {
                CollectionAdapter.Action.Select -> openCollection(collection)
                CollectionAdapter.Action.Rename -> renameCollection(collection)
                CollectionAdapter.Action.Delete -> deleteCollection(collection)
            }
        }

        viewModel.getCollections().observe(viewLifecycleOwner, { collection ->
            collectionAdapter.submitList(ArrayList(collection))
        })

        view.findViewById<RecyclerView>(R.id.recycler_collection).apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            adapter = collectionAdapter
        }

        scrim = view.findViewById<View>(R.id.scrim).apply { setOnClickListener { hideActionButtonMenu() } }
        actionButton = view.findViewById<View>(R.id.floating_action_button).apply { setOnClickListener { toggleActionButtonMenu() }}
        actionButtonNew = view.findViewById<View>(R.id.floating_action_new).apply { setOnClickListener { createCollectionDialog() }}
        actionButtonImport = view.findViewById<View>(R.id.floating_action_import).apply { setOnClickListener { importCollectionDialog() }}
    }

    private fun showActionButtonMenu() {
        scrim.visibility = View.VISIBLE
        if (actionButton.rotation == 0f) {
            ViewAnimation.rotate(actionButton, 150, 135f, 1f, 0.5f) { callback ->
                if (callback == ViewAnimation.State.Start) {
                    ViewAnimation.fadeIn(actionButtonNew, 150)
                    HandlerCompat.postDelayed(Handler(), { ViewAnimation.fadeIn(actionButtonImport, 150) }, null, 50)
                }
            }
        }
    }

    private fun hideActionButtonMenu() {
        scrim.visibility = View.INVISIBLE
        if (actionButton.rotation > 0f) {
            ViewAnimation.rotate(actionButton, 150, 0f, 0.5f, 1f) { callback ->
                if (callback == ViewAnimation.State.Start) {
                    HandlerCompat.postDelayed(Handler(), { ViewAnimation.fadeOut(actionButtonNew, 150) }, null, 50)
                    ViewAnimation.fadeOut(actionButtonImport, 150)
                }
            }
        }
    }

    private fun toggleActionButtonMenu() {
        if (actionButton.rotation == 0f)
            showActionButtonMenu()
        else
            hideActionButtonMenu()
    }

    override fun onStart() {
        super.onStart()
        // Initialize on start in case of external updates.
        viewModel.initialize(requireContext())
    }

    private fun openCollection(collection: Collection) {
        // Get progress bar from MainActivity.
        val progress = requireActivity().findViewById<LinearProgressIndicator>(R.id.progress_bar)

        // Check if dictionary is finished loading.
        if (progress.isIndeterminate) {
            Snackbar.make(requireActivity().findViewById(R.id.layout_root), getString(R.string.dictionary_wait), 1500).show()
            return
        }

        // Start new activity, fragment handles the result.
        val intent = Intent(activity, CollectionDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(CollectionDetailsActivity.ARGUMENT_COLLECTION_ID, collection.id)
        activityLauncher.launch(intent)
    }

    private fun renameCollection(collection: Collection) {
        RenameCollectionDialog(collection.name) { name ->
            collection.name = name
            viewModel.updateCollection(requireContext(), collection)
        }.show(requireActivity().supportFragmentManager, RenameCollectionDialog::class.simpleName)
    }

    private fun deleteCollection(collection: Collection) {
        viewModel.deleteCollection(requireContext(), collection) {
            Snackbar.make(requireActivity().findViewById(R.id.layout_root), String.format(getString(R.string.collection_delete), collection.name), 6000).setAction(getString(R.string.undo)) {
                // Undo the delete action.
                viewModel.addCollection(requireContext(), collection)
            }.show()
        }
    }

    private fun importCollectionDialog() {
        hideActionButtonMenu()
        ImportCollectionDialog { collection ->
            collection.id = 0
            viewModel.addCollection(requireContext(), collection)
        }.show(requireActivity().supportFragmentManager, ImportCollectionDialog::class.simpleName)
    }

    private fun createCollectionDialog() {
        hideActionButtonMenu()
        NameCollectionDialog { name ->
            viewModel.createCollection(requireContext(), name)
        }.show(requireActivity().supportFragmentManager, NameCollectionDialog::class.simpleName)
    }

    override fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK || result.data == null)
            return
        if (!result.data!!.hasExtra(CollectionDetailsActivity.ARGUMENT_COLLECTION_ID))
            return

        val id = result.data?.getLongExtra(CollectionDetailsActivity.ARGUMENT_COLLECTION_ID, 0) as Long
        val collection = viewModel.getCollection(id)
        deleteCollection(collection)
    }
}