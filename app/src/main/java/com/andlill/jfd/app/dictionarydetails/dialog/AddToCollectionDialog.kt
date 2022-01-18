package com.andlill.jfd.app.dictionarydetails.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.dictionarydetails.adapter.AddToCollectionAdapter
import com.andlill.jfd.app.shared.dialog.NameCollectionDialog
import com.andlill.jfd.utils.AppUtils
import com.google.android.material.snackbar.Snackbar

class AddToCollectionDialog(private val entryId: Int) : DialogFragment() {

    private lateinit var viewModel: AddToCollectionDialogViewModel

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_collection_add, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_MaterialComponents_Dialog_Alert).setView(layout).create()

        viewModel = ViewModelProvider(requireActivity())[AddToCollectionDialogViewModel::class.java]
        viewModel.initialize(requireContext(), entryId)

        val entry = viewModel.getDictionaryEntry()
        val collections = viewModel.getCollections()

        layout.findViewById<View>(R.id.layout_collection_new).setOnClickListener {
            NameCollectionDialog(getString(R.string.menu_item_collection_create), "") { name ->
                viewModel.createCollection(requireContext(), name)
                showMessageAdd(entry.getPrimaryReading(), name)
                dismiss()
            }.show(requireActivity().supportFragmentManager, NameCollectionDialog::class.simpleName)
        }

        layout.findViewById<RecyclerView>(R.id.recycler_collection).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AddToCollectionAdapter(collections, entry) { collection ->
                if (!collection.content.contains(entry.id)) {
                    collection.content.add(entry.id)
                    showMessageAdd(entry.getPrimaryReading(), collection.name)
                }
                else {
                    collection.content.remove(entry.id)
                    showMessageRemove(entry.getPrimaryReading(), collection.name)
                }

                viewModel.updateCollection(requireContext(), collection)
                AppUtils.postDelayed(100) {
                    dismiss()
                }
            }
        }

        return dialog
    }

    private fun showMessageAdd(entry: String, collection: String) {
        Snackbar.make(requireActivity().findViewById(R.id.layout_root), String.format(getString(R.string.dictionary_add_to_collection), entry, collection), 1500).show()
    }

    private fun showMessageRemove(entry: String, collection: String) {
        Snackbar.make(requireActivity().findViewById(R.id.layout_root), String.format(getString(R.string.dictionary_remove_from_collection), entry, collection), 1500).show()
    }
}