package com.andlill.jld.app.collectiondetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.collectiondetails.CollectionDetailsViewModel
import com.andlill.jld.model.Collection
import com.andlill.jld.model.DictionaryEntry
import com.google.android.material.color.MaterialColors

class CollectionContentAdapter(private val viewModel: CollectionDetailsViewModel, private val callback: (Action, DictionaryEntry) -> Unit) : RecyclerView.Adapter<CollectionContentAdapter.ViewHolder>() {

    enum class Action {
        Select,
        Selection,
    }

    private val selection = ArrayList<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_collection_content, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val collection = viewModel.getCollection().value as Collection
        val entry = viewModel.getDictionaryEntry(collection.content[position])
        holder.bind(position, entry)
        holder.itemView.setOnClickListener {
            callback(Action.Select, entry)
        }
        holder.itemView.setOnLongClickListener { view ->
            if (selection.contains(entry.id)) {
                selection.remove(entry.id)
                view.setBackgroundColor(MaterialColors.getColor(view, R.attr.colorSurface))
            }
            else {
                selection.add(entry.id)
                view.setBackgroundColor(MaterialColors.getColor(view, R.attr.colorControlHighlight))
            }
            callback(Action.Selection, entry)
            true
        }
    }

    override fun getItemCount(): Int {
        val collection = viewModel.getCollection().value as Collection
        return collection.content.size
    }

    fun indexOf(item: Int): Int {
        val collection = viewModel.getCollection().value as Collection
        return collection.content.indexOf(item)
    }

    fun getSelection(): List<Int> {
        return selection
    }

    fun clearSelection() {
        selection.clear()
    }

    fun cancelSelection() {
        val collection = viewModel.getCollection().value as Collection
        selection.forEach {
            val index = collection.content.indexOf(it)
            notifyItemChanged(index)
        }
        selection.clear()
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(index: Int, entry: DictionaryEntry) {
            view.setBackgroundColor(MaterialColors.getColor(view, R.attr.colorSurface))
            view.findViewById<TextView>(R.id.text_index).text = String.format("%d.", index + 1)

            if (entry.reading[0].kanji.isNotEmpty()) {
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