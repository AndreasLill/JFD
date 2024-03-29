package com.andlill.jfd.app.main

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.collectiondetails.CollectionDetailsActivity
import com.andlill.jfd.app.main.adapter.CollectionAdapter
import com.andlill.jfd.app.main.dialog.CollectionOptionsDialog
import com.andlill.jfd.app.main.dialog.ImportCollectionDialog
import com.andlill.jfd.app.shared.ResultActivityFragment
import com.andlill.jfd.app.shared.dialog.NameCollectionDialog
import com.andlill.jfd.model.Collection
import com.google.android.material.snackbar.Snackbar

class CollectionFragment : ResultActivityFragment() {

    private lateinit var viewModel: CollectionFragmentViewModel
    private lateinit var collectionAdapter: CollectionAdapter
    private lateinit var actionButton: View

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_collection, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(CollectionFragmentViewModel::class.java)

        collectionAdapter = CollectionAdapter { collection->
            openCollection(collection)
        }

        viewModel.getCollections().observe(viewLifecycleOwner) { collection ->
            collectionAdapter.submitList(collection.toList())
        }

        view.findViewById<RecyclerView>(R.id.recycler_collection).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = collectionAdapter
        }

        actionButton = view.findViewById<View>(R.id.floating_action_button).apply {
            setOnClickListener {
                CollectionOptionsDialog { action ->
                    when (action) {
                        CollectionOptionsDialog.Action.Import -> importCollectionDialog()
                        CollectionOptionsDialog.Action.New -> newCollectionDialog()
                    }
                }.show(requireActivity().supportFragmentManager, CollectionOptionsDialog::class.simpleName)
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Initialize on start in case of external updates.
        viewModel.initialize(requireContext())
    }

    private fun openCollection(collection: Collection) {
        // Start new activity, fragment handles the result.
        val intent = Intent(activity, CollectionDetailsActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        intent.putExtra(CollectionDetailsActivity.ARGUMENT_COLLECTION_ID, collection.id)
        activityLauncher.launch(intent)
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
        ImportCollectionDialog { collection ->
            collection.id = 0
            viewModel.addCollection(requireContext(), collection)
        }.show(requireActivity().supportFragmentManager, ImportCollectionDialog::class.simpleName)
    }

    private fun newCollectionDialog() {
        NameCollectionDialog(getString(R.string.menu_item_collection_create), "") { name ->
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