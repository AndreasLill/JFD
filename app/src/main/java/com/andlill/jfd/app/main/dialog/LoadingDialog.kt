package com.andlill.jfd.app.main.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.andlill.jfd.R
import com.andlill.jfd.utils.AppUtils

class LoadingDialog : DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.dialog_loading, container, false)
    }

    override fun onResume() {
        super.onResume()

        // Set status bar to dark if not in night mode.
        if (!AppUtils.isDarkMode(requireActivity())) {
            AppUtils.setStatusBarDark(requireActivity())
        }
    }

    override fun onStop() {
        // Set status bar back to light if not in night mode.
        if (!AppUtils.isDarkMode(requireActivity())) {
            AppUtils.setStatusBarLight(requireActivity())
        }

        super.onStop()
    }
}