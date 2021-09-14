package com.andlill.jld.app.settings

import android.content.Context
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
import com.andlill.jld.utils.AppPreferences
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
        viewModel.initialize(getSharedPreferences(AppPreferences.PREFERENCES, Context.MODE_PRIVATE))

        viewModel.getSharedPreferences().observe(this, {
            initializeSettingDarkMode()
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

    private fun initializeSettingDarkMode() {
        findViewById<View>(R.id.setting_dark_mode).apply {
            // Get shared preference value or default.
            val value = viewModel.getSharedPreferenceString(AppPreferences.KEY_DARK_MODE, AppPreferences.DarkModeOptions[0])
            // Setup views.
            findViewById<ImageView>(R.id.image_icon).setImageDrawable(ContextCompat.getDrawable(this@SettingsActivity, R.drawable.ic_dark_mode))
            findViewById<TextView>(R.id.text_title).text = getString(R.string.dark_mode)
            findViewById<TextView>(R.id.setting_value).text = value
            // Setup dialog.
            setOnClickListener {
                if (!isDialogVisible()) {
                    settingsDialog = SettingsDialog(getString(R.string.dark_mode), AppPreferences.DarkModeOptions, value) { result ->
                        viewModel.setSharedPreferences(AppPreferences.KEY_DARK_MODE, result)
                        when (result) {
                            AppPreferences.DarkModeOptions[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
                            AppPreferences.DarkModeOptions[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                            AppPreferences.DarkModeOptions[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                        }
                    }
                    settingsDialog.show(supportFragmentManager, SettingsDialog::class.simpleName)
                }
            }
        }
    }
}