package com.andlill.jfd.app.main.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.model.SearchHistory

class SearchHistoryAdapter(private val callback: (Action, SearchHistory) -> Unit) : ListAdapter<SearchHistory, SearchHistoryAdapter.ViewHolder>(DiffCallback()) {

    enum class Action {
        Select,
        Delete
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_search_history, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val searchHistory = getItem(position)
        holder.bind(searchHistory)
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(searchHistory: SearchHistory) {
            view.findViewById<TextView>(R.id.text_history).text = searchHistory.value

            // Listener on view.
            view.setOnClickListener {
                callback(Action.Select, searchHistory)
            }
            // Listener on delete view.
            view.findViewById<View>(R.id.image_delete).setOnClickListener {
                callback(Action.Delete, searchHistory)
            }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<SearchHistory>() {
        override fun areItemsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: SearchHistory, newItem: SearchHistory): Boolean {
            return oldItem.value == oldItem.value
        }
    }
}