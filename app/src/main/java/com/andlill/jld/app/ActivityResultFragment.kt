package com.andlill.jld.app

import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment

abstract class ActivityResultFragment(layout: Int) : Fragment(layout) {

    protected val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> handleActivityResult(result) }
    abstract fun handleActivityResult(result: ActivityResult)
}