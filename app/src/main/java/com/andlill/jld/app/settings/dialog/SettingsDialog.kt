package com.andlill.jld.app.settings.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.settings.adapter.SettingsAdapter
import com.andlill.jld.utils.AppUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import java.util.*

class SettingsDialog(private val title: String, private val settings: List<String>, private val selected: String, private val callback: (String) -> Unit) : BottomSheetDialogFragment() {

    private var value: String = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = layoutInflater.inflate(R.layout.dialog_settings, container, false)

        val settingsAdapter = SettingsAdapter(settings, selected) { result ->
            value = result
            AppUtils.postDelayed(100) {
                dismiss()
            }
        }

        layout.findViewById<TextView>(R.id.text_title).text = title.toUpperCase(Locale.ROOT)
        layout.findViewById<RecyclerView>(R.id.recycler_settings).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = settingsAdapter
        }

        return layout
    }

    override fun onDestroy() {
        super.onDestroy()

        if (value.isNotEmpty())
            callback(value)
    }
}