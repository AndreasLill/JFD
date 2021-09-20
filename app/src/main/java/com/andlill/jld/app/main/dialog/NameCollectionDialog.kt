package com.andlill.jld.app.main.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.andlill.jld.R
import com.andlill.jld.app.shared.dialog.DialogResult
import com.andlill.jld.utils.AppUtils
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class NameCollectionDialog(private val callback: (DialogResult, String) -> Unit) : DialogFragment(), DialogInterface.OnShowListener {

    private lateinit var inputLayout: TextInputLayout
    private lateinit var input: TextInputEditText

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_collection_name, null)
        layout.findViewById<TextView>(R.id.text_title).text = getString(R.string.menu_item_collection_new)
        val dialog = AlertDialog.Builder(requireContext(), R.style.Theme_MaterialComponents_Dialog_Alert).setView(layout).create()
        dialog.setOnShowListener(this)

        inputLayout = layout.findViewById(R.id.layout_input)
        input = layout.findViewById(R.id.input)
        input.addTextChangedListener { text -> validateInput(text.toString()) }
        input.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_GO || action == EditorInfo.IME_ACTION_DONE) {
                if (validateInput(input.text.toString())) {
                    callback(DialogResult.OK, input.text.toString().trim())
                    dismiss()
                }
            }
            false
        }

        // Set listener on OK button.
        layout.findViewById<View>(R.id.button_ok).setOnClickListener {
            if (validateInput(input.text.toString())) {
                callback(DialogResult.OK, input.text.toString().trim())
                dismiss()
            }
        }
        // Set listener on Cancel button.
        layout.findViewById<View>(R.id.button_cancel).setOnClickListener {
            dismiss()
        }

        return dialog
    }

    override fun onShow(dialog: DialogInterface?) {
        AppUtils.showSoftInput(requireActivity(), input)
    }

    override fun onDismiss(dialog: DialogInterface) {
        AppUtils.hideSoftInput(requireActivity(), input)
        super.onDismiss(dialog)
        callback(DialogResult.Dismiss, "")
    }

    private fun validateInput(text: String): Boolean {
        return if (Regex("^[A-Za-z0-9 ]{1,30}\$").matches(text)) {
            inputLayout.isErrorEnabled = false
            true
        }
        else {
            inputLayout.error = getString(R.string.validation_error_collection_name)
            false
        }
    }
}