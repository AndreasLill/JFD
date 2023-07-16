package com.andlill.jfd.app.collectiondetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.collectiondetails.CollectionDetailsActivityViewModel
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import com.google.android.material.color.MaterialColors

class CollectionContentAdapter(private val viewModel: CollectionDetailsActivityViewModel, private val callback: (Action, DictionaryEntry) -> Unit) : RecyclerView.Adapter<CollectionContentAdapter.ViewHolder>() {

    enum class Action {
        Select,
        SelectionStart,
        SelectionUpdate,
        SelectionEnd,
    }

    private val selection = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_collection_content, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collection = viewModel.getCollection().value as Collection
        val entry = viewModel.getDictionaryEntry(holder.itemView.context, collection.content[position])
        holder.bind(position, entry)
        holder.itemView.setOnClickListener {
            callback(Action.Select, entry)
        }
        holder.itemView.setOnLongClickListener { view ->
            // Start selection mode if empty.
            if (selection.isEmpty())
                callback(Action.SelectionStart, entry)

            // Update by adding or removing entry to selection.
            updateSelection(view, entry)

            // End selection mode if empty after update.
            if (selection.isEmpty())
                callback(Action.SelectionEnd, entry)
            else
                callback(Action.SelectionUpdate, entry)

            true
        }
    }

    override fun getItemCount(): Int {
        val collection = viewModel.getCollection().value as Collection
        return collection.content.size
    }

    private fun updateSelection(view: View, entry: DictionaryEntry) {
        if (selection.contains(entry.id)) {
            selection.remove(entry.id)
            view.setBackgroundColor(MaterialColors.getColor(view, R.attr.colorItemBackground))
        }
        else {
            selection.add(entry.id)
            view.setBackgroundColor(MaterialColors.getColor(view, R.attr.colorControlHighlight))
        }
    }

    fun indexOf(item: Int): Int {
        val collection = viewModel.getCollection().value as Collection
        return collection.content.indexOf(item)
    }

    fun getSelection(): List<Int> {
        return selection
    }

    fun clearSelection() {
        val collection = viewModel.getCollection().value as Collection
        selection.forEach {
            val index = collection.content.indexOf(it)
            notifyItemChanged(index)
        }
        selection.clear()
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(index: Int, entry: DictionaryEntry) {
            view.setBackgroundColor(MaterialColors.getColor(view, R.attr.colorItemBackground))
            view.findViewById<TextView>(R.id.text_index).text = String.format("%d.", index + 1)

            if (!entry.reading[0].kanji.isNullOrEmpty()) {
                view.findViewById<TextView>(R.id.text_kanji).text = entry.reading[0].kanji
                view.findViewById<TextView>(R.id.text_kana).text = String.format("【%s】", entry.reading[0].kana)
            } else {
                view.findViewById<TextView>(R.id.text_kanji).text = entry.reading[0].kana
                view.findViewById<TextView>(R.id.text_kana).text = ""
            }

            if (entry.sense.isNotEmpty())
                view.findViewById<TextView>(R.id.text_translation).text = entry.sense[0].glossary.joinToString("; ")
        }
    }
}