package com.andlill.jld.app.dictionarydetails.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.model.DictionaryEntry

class ReadingAdapter(private val dataSet: List<DictionaryEntry.Reading>) : RecyclerView.Adapter<ReadingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_reading, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position])
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(reading: DictionaryEntry.Reading) {
            if (reading.kanji.isNotEmpty()) {
                view.findViewById<TextView>(R.id.text_kanji).text = reading.kanji
                view.findViewById<TextView>(R.id.text_kana).text = String.format("【%s】", reading.kana)
            } else {
                view.findViewById<TextView>(R.id.text_kanji).text = reading.kana
                view.findViewById<TextView>(R.id.text_kana).visibility = View.GONE
            }
        }
    }
}