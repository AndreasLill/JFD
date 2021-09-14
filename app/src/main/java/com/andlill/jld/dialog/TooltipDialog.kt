package com.andlill.jld.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.andlill.jld.R

class TooltipDialog(private val targetView: View, private val tooltipText: String) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val layout = requireActivity().layoutInflater.inflate(R.layout.dialog_tooltip, null)
        val dialog = AlertDialog.Builder(requireActivity(), R.style.AppStyle_Dialog_ToolTip).setView(layout).create()
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL, WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL)
        dialog.window?.setFlags(WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH, WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH)
        dialog.setCanceledOnTouchOutside(true)

        layout.findViewById<TextView>(R.id.text_tooltip).text = tooltipText
        layout.setOnClickListener {
            dialog.dismiss()
        }

        return dialog
    }
}