package com.andlill.jld.app.main.dialog

import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Base64
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import com.andlill.jld.R
import com.andlill.jld.app.shared.dialog.DialogResult
import com.andlill.jld.model.Collection
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.gson.Gson
import java.lang.Exception

class ImportCollectionDialog(private val callback: (DialogResult, Collection?) -> Unit) : DialogFragment() {

    private var collection: Collection? = null
    private lateinit var inputLayout: TextInputLayout
    private lateinit var input: TextInputEditText
    private lateinit var collectionLayout: View
    private lateinit var collectionTextTitle: TextView
    private lateinit var collectionTextSubtitle: TextView

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_collection_import, null)
        val dialog = AlertDialog.Builder(requireActivity(), R.style.Theme_MaterialComponents_Dialog_Alert).setView(layout).create()

        inputLayout = layout.findViewById(R.id.layout_input)
        input = layout.findViewById(R.id.input)
        input.addTextChangedListener { text -> validateInput(text.toString()) }
        input.setOnEditorActionListener { _, action, _ ->
            if (action == EditorInfo.IME_ACTION_GO || action == EditorInfo.IME_ACTION_DONE) {
                if (validateInput(input.text.toString())) {
                    callback(DialogResult.OK, collection)
                    requireDialog().dismiss()
                }
            }
            false
        }

        collectionLayout = layout.findViewById(R.id.layout_collection)
        collectionTextTitle = layout.findViewById(R.id.text_collection_title)
        collectionTextSubtitle = layout.findViewById(R.id.text_collection_subtitle)

        // Set listener on OK button.
        layout.findViewById<View>(R.id.button_ok).setOnClickListener {
            if (validateInput(input.text.toString())) {
                callback(DialogResult.OK, collection)
                requireDialog().dismiss()
            }
        }
        // Set listener on Cancel button.
        layout.findViewById<View>(R.id.button_cancel).setOnClickListener {
            requireDialog().dismiss()
        }

        return dialog
    }

    private fun validateInput(text: String): Boolean {
        return if (Regex("^(?:[A-Za-z0-9+/]{4})*(?:[A-Za-z0-9+/]{2}==|[A-Za-z0-9+/]{3}=)?\$").matches(text)) {
            collection = tryDecodeImport(text)
            if (collection != null) {
                inputLayout.isErrorEnabled = false
                collectionLayout.visibility = View.VISIBLE
                collectionTextTitle.text = collection?.name
                collectionTextSubtitle.text = String.format(requireActivity().getString(R.string.items), collection?.content?.size)
                true
            }
            else {
                collectionLayout.visibility = View.GONE
                inputLayout.error = requireActivity().getString(R.string.validation_error_collection_import)
                false
            }
        }
        else {
            collection = null
            collectionLayout.visibility = View.GONE
            inputLayout.error = requireActivity().getString(R.string.validation_error_collection_import)
            false
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        callback(DialogResult.Dismiss, null)
    }

    private fun tryDecodeImport(str: String): Collection? {
        return try {
            val bytes = Base64.decode(str.trim(), Base64.NO_WRAP)
            Gson().fromJson(String(bytes), Collection::class.java)
        }
        catch (ex: Exception) {
            null
        }
    }
}