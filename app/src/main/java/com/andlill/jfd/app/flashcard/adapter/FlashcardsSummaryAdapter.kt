package com.andlill.jfd.app.flashcard.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.model.DictionaryEntry
import com.google.android.material.color.MaterialColors

class FlashcardsSummaryAdapter : ListAdapter<Pair<DictionaryEntry, Boolean>, FlashcardsSummaryAdapter.ViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_flashcards_summary, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(item: Pair<DictionaryEntry, Boolean>) {
            view.findViewById<TextView>(R.id.text_value).text = item.first.getPrimaryReading()
            view.findViewById<TextView>(R.id.text_result).apply {
                text = if (item.second) "✓" else "✗"

                if (!item.second)
                    setTextColor(MaterialColors.getColor(view, R.attr.colorError))
                else
                    setTextColor(MaterialColors.getColor(view, R.attr.colorCheckMark))
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Pair<DictionaryEntry, Boolean>>() {

        override fun areItemsTheSame(oldItem: Pair<DictionaryEntry, Boolean>, newItem: Pair<DictionaryEntry, Boolean>): Boolean {
            return oldItem.first.id == newItem.first.id && oldItem.second == newItem.second
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Pair<DictionaryEntry, Boolean>, newItem: Pair<DictionaryEntry, Boolean>): Boolean {
            return oldItem == newItem
        }
    }
}