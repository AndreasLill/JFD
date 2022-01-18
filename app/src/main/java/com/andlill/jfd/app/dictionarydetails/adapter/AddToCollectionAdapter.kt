package com.andlill.jfd.app.dictionarydetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.model.Collection
import com.andlill.jfd.model.DictionaryEntry
import com.google.android.material.color.MaterialColors

class AddToCollectionAdapter(private val dataSet: List<Collection>, private val entry: DictionaryEntry, private val callback: (Collection) -> Unit) : RecyclerView.Adapter<AddToCollectionAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_collection_add, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
        holder.itemView.setOnClickListener {
            holder.toggleIcon(dataSet[position])
            callback(dataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    inner class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(collection: Collection) {
            view.findViewById<TextView>(R.id.text_title).text = collection.name

            if (collection.content.contains(entry.id)) {
                val imageIcon = view.findViewById<ImageView>(R.id.image_icon)
                imageIcon.setImageResource(R.drawable.ic_collection_check)
                imageIcon.setColorFilter(MaterialColors.getColor(view, R.attr.colorCheckMark))
            }
        }

        fun toggleIcon(collection: Collection) {
            val imageIcon = view.findViewById<ImageView>(R.id.image_icon)

            if (!collection.content.contains(entry.id)) {
                imageIcon.setImageResource(R.drawable.ic_collection_check)
                imageIcon.setColorFilter(MaterialColors.getColor(view, R.attr.colorCheckMark))
            }
            else {
                imageIcon.setImageResource(R.drawable.ic_collection_add)
                imageIcon.setColorFilter(MaterialColors.getColor(view, R.attr.colorHint))
            }
        }
    }
}