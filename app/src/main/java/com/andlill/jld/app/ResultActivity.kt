package com.andlill.jld.app

import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity

abstract class ResultActivity : AppCompatActivity() {
    protected val activityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result -> handleActivityResult(result) }
    abstract fun handleActivityResult(result: ActivityResult)
}