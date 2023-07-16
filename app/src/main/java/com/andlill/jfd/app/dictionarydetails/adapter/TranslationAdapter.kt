package com.andlill.jfd.app.dictionarydetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.language.Language
import com.andlill.jfd.model.DictSense

class TranslationAdapter(private val dataSet: List<DictSense>) : RecyclerView.Adapter<TranslationAdapter.ViewHolder>() {

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

        fun bind(index: Int, sense: DictSense) {
            view.findViewById<TextView>(R.id.text_type).text = sense.partOfSpeech.joinToString(", ") { Language.partOfSpeech.getValue(it) }
            view.findViewById<TextView>(R.id.text_number).text = String.format("%d.", index+1)
            view.findViewById<TextView>(R.id.text_translation).text = sense.glossary.joinToString("; ")
        }
    }
}