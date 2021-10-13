package com.andlill.jld.app.main.adapter

import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.model.DictionaryEntry
import com.andlill.jld.utils.AppUtils
import com.google.android.material.color.MaterialColors

class DictionaryAdapter(private val callback: (DictionaryEntry) -> Unit) : RecyclerView.Adapter<DictionaryAdapter.ViewHolder>() {

    private val dataSet: ArrayList<DictionaryEntry> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_dictionary, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entry = dataSet[position]
        holder.bind(entry)
        holder.itemView.setOnClickListener {
            callback(entry)
        }
        holder.itemView.setOnCreateContextMenuListener { contextMenu, view, _ ->
            if (entry.reading[0].kanji.isNotEmpty()) {
                contextMenu?.setHeaderTitle(String.format(view.context.getString(R.string.context_menu_options_title), entry.reading[0].kanji))
                contextMenu?.add(Menu.NONE, 1, Menu.NONE, view.context.getString(R.string.menu_item_copy_kanji))?.setOnMenuItemClickListener { AppUtils.copyToClipboard(view, entry.reading[0].kanji); true }
                contextMenu?.add(Menu.NONE, 2, Menu.NONE, view.context.getString(R.string.menu_item_copy_kana))?.setOnMenuItemClickListener { AppUtils.copyToClipboard(view, entry.reading[0].kana); true }
            }
        }
    }

    override fun getItemCount(): Int {
        return dataSet.size
    }

    fun dataSet(): ArrayList<DictionaryEntry> {
        return dataSet
    }

    fun update(data: ArrayList<DictionaryEntry>) {
        dataSet.clear()
        dataSet.addAll(data)
        notifyDataSetChanged()
    }

    class ViewHolder(private val view: View): RecyclerView.ViewHolder(view) {

        fun bind(entry: DictionaryEntry) {

            val layoutRoot = view.findViewById<View>(R.id.layout_root)
            val viewLabel = view.findViewById<View>(R.id.view_label)

            if (entry.isCommon()) {
                layoutRoot.alpha = 1f
                layoutRoot.setBackgroundColor(MaterialColors.getColor(layoutRoot, R.attr.colorDictionaryCommonHint))
                viewLabel.setBackgroundColor(MaterialColors.getColor(viewLabel, R.attr.colorDictionaryCommonTag))
            }
            else {
                layoutRoot.alpha = 0.7f
                layoutRoot.setBackgroundColor(MaterialColors.getColor(layoutRoot, R.attr.colorSurface))
                viewLabel.setBackgroundColor(MaterialColors.getColor(viewLabel, R.attr.colorSurface))
            }

            if (entry.reading[0].kanji.isNotEmpty()) {
                view.findViewById<TextView>(R.id.text_kanji).text = entry.reading[0].kanji
                view.findViewById<TextView>(R.id.text_kana).text = String.format("【%s】", entry.reading[0].kana)
            }
            else {
                view.findViewById<TextView>(R.id.text_kanji).text = entry.reading[0].kana
                view.findViewById<TextView>(R.id.text_kana).text = ""
            }

            if (entry.sense.isNotEmpty())
                view.findViewById<TextView>(R.id.text_translation).text = entry.sense[0].glossary.joinToString("; ")
        }
    }
}