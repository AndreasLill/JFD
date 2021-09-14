package com.andlill.jld.app.dictionarydetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.model.DictionaryEntry

class TranslationAdapter(private val dataSet: List<DictionaryEntry.Sense>) : RecyclerView.Adapter<TranslationAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_translation, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position, dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(index: Int, sense: DictionaryEntry.Sense) {
            view.findViewById<TextView>(R.id.text_type).text = sense.partOfSpeech.joinToString(", ")
            view.findViewById<TextView>(R.id.text_translation).text = String.format("%d. %s", index+1, sense.glossary.joinToString("; "))
        }
    }
}