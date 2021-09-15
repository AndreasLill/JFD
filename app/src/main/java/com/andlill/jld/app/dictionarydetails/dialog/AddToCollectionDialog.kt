package com.andlill.jld.app.dictionarydetails.dialog

import android.app.Dialog
import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.os.HandlerCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.dictionarydetails.adapter.AddToCollectionAdapter
import com.andlill.jld.app.dictionarydetails.DictionaryDetailsViewModel
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry

class AddToCollectionDialog(private val viewModel: DictionaryDetailsViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_collection_add, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_AppCompat_Light_Dialog_Alert).setView(layout).create()

        val entry = viewModel.getDictionaryEntry().value as DictionaryEntry
        val collections = viewModel.getCollections().value as ArrayList<Collection>

        layout.findViewById<RecyclerView>(R.id.recycler_collection).apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = AddToCollectionAdapter(collections, entry) { collection ->
                if (!collection.content.contains(entry.id)) {
                    collection.content.add(entry.id)
                }
                else {
                    collection.content.remove(entry.id)
                }

                viewModel.updateCollection(requireContext(), collection)
                HandlerCompat.postDelayed(Handler(), {requireDialog().dismiss()}, null, 100)
            }
        }

        layout.findViewById<View>(R.id.button_close).setOnClickListener {
            requireDialog().dismiss()
        }

        return dialog
    }
}