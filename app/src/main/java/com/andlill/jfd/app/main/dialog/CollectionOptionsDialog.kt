package com.andlill.jfd.app.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.andlill.jfd.R
import com.andlill.jfd.utils.AppUtils
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class CollectionOptionsDialog(private val callback: (Action) -> Unit) : BottomSheetDialogFragment()  {

    enum class Action {
        Import,
        New,
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val layout = layoutInflater.inflate(R.layout.dialog_collection_options, container, false)

        layout.findViewById<View>(R.id.button_import).setOnClickListener {
            AppUtils.postDelayed(150) {
                callback(Action.Import)
                dismiss()
            }
        }
        layout.findViewById<View>(R.id.button_new).setOnClickListener {
            AppUtils.postDelayed(150) {
                callback(Action.New)
                dismiss()
            }
        }

        return layout
    }
}