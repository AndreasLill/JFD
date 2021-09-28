package com.andlill.jld.app.settings.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.os.Handler
import android.view.Gravity
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.os.HandlerCompat
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.settings.adapter.SettingsAdapter
import com.andlill.jld.utils.AppUtils
import java.util.*

class SettingsDialog(private val title: String, private val settings: List<String>, private val selected: String, private val callback: (String) -> Unit) : DialogFragment() {

    private var value: String = ""

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_settings, null)
        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_MaterialComponents_Dialog_Alert).setView(layout).create()
        dialog.window?.setGravity(Gravity.BOTTOM)
        dialog.window?.attributes?.windowAnimations = R.style.DialogAnimationSlide

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

        return dialog
    }

    override fun onDestroy() {
        super.onDestroy()

        if (value.isNotEmpty())
            callback(value)
    }
}