package com.andlill.jld.app.settings

import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.andlill.jld.R
import com.andlill.jld.app.settings.dialog.SettingsDialog
import com.andlill.jld.io.repository.SharedPreferencesRepository
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsActivityViewModel
    private lateinit var settingsDialog: SettingsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.menu_item_settings)

        viewModel = ViewModelProvider(this).get(SettingsActivityViewModel::class.java)
        viewModel.initialize(this)

        viewModel.getDarkMode().observe(this, { value ->
            initializeSettingDarkMode(value)
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }

    private fun isDialogVisible() : Boolean {
        return this::settingsDialog.isInitialized && settingsDialog.isResumed
    }

    private fun initializeSettingDarkMode(value: String) {
        findViewById<View>(R.id.setting_dark_mode).apply {
            // Setup views.
            findViewById<ImageView>(R.id.image_icon).setImageDrawable(ContextCompat.getDrawable(this@SettingsActivity, R.drawable.ic_dark_mode))
            findViewById<TextView>(R.id.text_title).text = getString(R.string.dark_mode)
            findViewById<TextView>(R.id.setting_value).text = value
            // Setup click listener for settings dialog.
            setOnClickListener {
                if (!isDialogVisible()) {
                    settingsDialog = SettingsDialog(getString(R.string.dark_mode), SharedPreferencesRepository.OPTIONS_DARK_MODE, value) { result ->
                        // Handle results from settings dialog.
                        viewModel.setDarkMode(this@SettingsActivity, result)
                        when (result) {
                            SharedPreferencesRepository.OPTIONS_DARK_MODE[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            SharedPreferencesRepository.OPTIONS_DARK_MODE[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            SharedPreferencesRepository.OPTIONS_DARK_MODE[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    }
                    settingsDialog.show(supportFragmentManager, SettingsDialog::class.simpleName)
                }
            }
        }
    }
}