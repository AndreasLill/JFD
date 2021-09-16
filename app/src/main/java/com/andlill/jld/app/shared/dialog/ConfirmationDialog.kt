package com.andlill.jld.app.shared.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.andlill.jld.R

class ConfirmationDialog(private val dialogBody: String, private val callback: () -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_confirmation, null)
        val dialog = AlertDialog.Builder(requireActivity(), R.style.Theme_MaterialComponents_Dialog_Alert).setView(layout).create()

        layout.findViewById<TextView>(R.id.text_title).text = getString(R.string.dialog_confirm)
        layout.findViewById<TextView>(R.id.text_body).text = dialogBody

        // Set listener on OK button.
        layout.findViewById<View>(R.id.button_ok).setOnClickListener {
            callback()
            requireDialog().dismiss()
        }
        // Set listener on Cancel button.
        layout.findViewById<View>(R.id.button_cancel).setOnClickListener {
            requireDialog().dismiss()
        }

        return dialog
    }
}