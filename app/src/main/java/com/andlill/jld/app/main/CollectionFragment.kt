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
import com.andlill.jld.app.main.adapter.CollectionAdapter
import com.andlill.jld.utils.ViewAnimation
import com.andlill.jld.app.collectiondetails.CollectionDetailsActivity
import com.andlill.jld.dialog.DialogResult
import com.andlill.jld.app.main.dialog.ImportCollectionDialog
import com.andlill.jld.app.main.dialog.NameCollectionDialog
import com.andlill.jld.app.ActivityResultFragment
import com.google.android.material.snackbar.Snackbar

class CollectionFragment : ActivityResultFragment(R.layout.fragment_collection) {

    private lateinit var viewModel: CollectionFragmentViewModel
    private lateinit var collectionAdapter: CollectionAdapter

    private lateinit var scrim: View
    private lateinit var actionButton: View
    private lateinit var actionButtonNew: View
    private lateinit var actionButtonImport: View

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CollectionFragmentViewModel::class.java)

        collectionAdapter = CollectionAdapter { collection->
            val activity = requireActivity() as MainActivity
            // Check if activity is ready.
            if (activity.isDictionaryReady()) {
                // Start new activity, fragment handles the result.
                val intent = Intent(activity, CollectionDetailsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                intent.putExtra(CollectionDetailsActivity.ARGUMENT_COLLECTION_ID, collection.id)
                activityLauncher.launch(intent)
            }
            else {
                Snackbar.make(activity.findViewById(android.R.id.content), getString(R.string.dictionary_wait), 1500).show()
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

    private fun importCollectionDialog() {
        hideActionButtonMenu()
        ImportCollectionDialog { result, collection ->
            when (result) {
                DialogResult.OK -> {
                    if (collection != null) {
                        collection.id = 0
                        viewModel.addCollection(requireContext(), collection)
                    }
                }
                DialogResult.Dismiss -> {}
            }
        }.show(requireActivity().supportFragmentManager, ImportCollectionDialog::class.simpleName)
    }

    private fun createCollectionDialog() {
        hideActionButtonMenu()
        NameCollectionDialog { result, name ->
            when (result) {
                DialogResult.OK -> viewModel.createCollection(requireContext(), name)
                DialogResult.Dismiss -> {}
            }
        }.show(requireActivity().supportFragmentManager, NameCollectionDialog::class.simpleName)
    }

    override fun handleActivityResult(result: ActivityResult) {
        if (result.resultCode == Activity.RESULT_OK) {
            if (result.data != null && result.data?.hasExtra(CollectionDetailsActivity.ARGUMENT_COLLECTION_ID) == true) {
                val id = result.data?.getLongExtra(CollectionDetailsActivity.ARGUMENT_COLLECTION_ID, 0) as Long
                val collection = viewModel.getCollection(id)

                viewModel.deleteCollection(requireContext(), collection) {
                    Snackbar.make(requireActivity().findViewById(android.R.id.content), String.format(getString(R.string.collection_delete), collection.name), 6000).setAction(getString(R.string.undo)) {
                        // Undo the delete action.
                        viewModel.addCollection(requireContext(), collection)
                    }.show()
                }
            }
        }
    }
}