package com.andlill.jfd.app.main.dialog

import android.app.Dialog
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.andlill.jfd.R

class AboutDialog : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_about, null)
        val dialog = AlertDialog.Builder(requireActivity(), R.style.Theme_MaterialComponents_Dialog_Alert).setView(layout).create()

        layout.findViewById<View>(R.id.button_close).setOnClickListener { dismiss() }

        layout.findViewById<TextView>(R.id.text_edict).apply {
            text = Html.fromHtml(getString(R.string.legal_edict))
            movementMethod = LinkMovementMethod.getInstance()
        }

        return dialog
    }
}