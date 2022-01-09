package com.andlill.jfd.app.dictionarydetails.dialog

import android.app.Dialog
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.dictionarydetails.DictionaryDetailsViewModel
import com.andlill.jfd.app.dictionarydetails.adapter.AddToCollectionAdapter
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import com.andlill.jfd.utils.AppUtils
import com.google.android.material.snackbar.Snackbar

class AddToCollectionDialog(private val viewModel: DictionaryDetailsViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_collection_add, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_MaterialComponents_Dialog_Alert).setView(layout).create()

        val entry = viewModel.getDictionaryEntry().value as DictionaryEntry
        val collections = viewModel.getCollections().value as ArrayList<Collection>

        layout.findViewById<RecyclerView>(R.id.recycler_collection).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = AddToCollectionAdapter(collections, entry) { collection ->
                if (!collection.content.contains(entry.id)) {
                    collection.content.add(entry.id)
                    Snackbar.make(requireActivity().findViewById(R.id.layout_root), String.format(getString(R.string.dictionary_add_to_collection), entry.getPrimaryReading(), collection.name), 1000).show()
                }
                else {
                    collection.content.remove(entry.id)
                    Snackbar.make(requireActivity().findViewById(R.id.layout_root), String.format(getString(R.string.dictionary_remove_from_collection), entry.getPrimaryReading(), collection.name), 1000).show()
                }

                viewModel.updateCollection(requireContext(), collection)
                AppUtils.postDelayed(100) {
                    dismiss()
                }
            }
        }

        return dialog
    }
}