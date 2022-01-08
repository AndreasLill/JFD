package com.andlill.jfd.app.shared

import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

abstract class ResultActivityFragment(layout: Int) : Fragment(layout) {

    protected val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> handleActivityResult(result) }
    abstract fun handleActivityResult(result: ActivityResult)
}