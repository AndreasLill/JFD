package com.andlill.jfd.app.dictionarydetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.model.Kanji

class KanjiAdapter(private val dataSet: List<Kanji>) : RecyclerView.Adapter<KanjiAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_kanji, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(kanji: Kanji) {
            view.findViewById<TextView>(R.id.text_kanji).text = kanji.character
            view.findViewById<TextView>(R.id.text_meaning).text = kanji.meaning.joinToString("; ")
        }

    }
}