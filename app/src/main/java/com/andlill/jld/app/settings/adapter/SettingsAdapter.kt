package com.andlill.jld.app.settings.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatRadioButton
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R

class SettingsAdapter(private val dataSet: List<String>, private val selected: String, private val callback: (String) -> Unit) : RecyclerView.Adapter<SettingsAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_settings, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(dataSet[position] == selected, dataSet[position])
        holder.itemView.setOnClickListener {
            callback(dataSet[position])
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {

        fun bind(selected: Boolean, title: String) {
            val radiobutton = view.findViewById<AppCompatRadioButton>(R.id.radiobutton)
            radiobutton.isChecked = selected
            radiobutton.text = title
        }
    }
}