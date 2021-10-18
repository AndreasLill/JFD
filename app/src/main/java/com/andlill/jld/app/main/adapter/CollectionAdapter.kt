package com.andlill.jld.app.main.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.model.Collection

class CollectionAdapter(private val callback: (Action, Collection) -> Unit) : ListAdapter<Collection, CollectionAdapter.ViewHolder>(DiffCallback()) {

    enum class Action {
        Select,
        Share,
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_collection, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item)
        holder.itemView.setOnClickListener {
            callback(Action.Select, item)
        }
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(collection: Collection) {
            view.findViewById<TextView>(R.id.text_title).text = collection.name

            if (collection.content.isNotEmpty())
                view.findViewById<TextView>(R.id.text_subtitle).text = String.format(view.context.getString(R.string.item_count), collection.content.size)
            else
                view.findViewById<TextView>(R.id.text_subtitle).text = view.context.getString(R.string.empty)

            view.findViewById<View>(R.id.button_menu).setOnClickListener { callback(Action.Share, collection) }
        }
    }

    private class DiffCallback : DiffUtil.ItemCallback<Collection>() {

        override fun areItemsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem.id == newItem.id
        }

        @SuppressLint("DiffUtilEquals")
        override fun areContentsTheSame(oldItem: Collection, newItem: Collection): Boolean {
            return oldItem == newItem
        }
    }
}