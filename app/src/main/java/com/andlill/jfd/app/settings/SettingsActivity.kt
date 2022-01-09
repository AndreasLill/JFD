package com.andlill.jfd.app.settings

import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import com.andlill.jfd.R
import com.andlill.jfd.app.settings.dialog.SettingsDialog
import com.andlill.jfd.utils.AppSettings
import com.andlill.jfd.utils.AppUtils
import java.util.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var viewModel: SettingsActivityViewModel
    private lateinit var settingsDialog: SettingsDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        findViewById<View>(R.id.button_back).setOnClickListener { finish() }

        viewModel = ViewModelProvider(this).get(SettingsActivityViewModel::class.java)
        viewModel.initialize(this)

        viewModel.getDarkMode().observe(this, { key ->
            settingDarkMode(key)
        })

        viewModel.getTextToSpeech().observe(this, { value ->
            settingTextToSpeech(value)
        })
    }

    override fun onResume() {
        super.onResume()

        // Set status bar to dark if not in night mode.
        if (!AppUtils.isDarkMode(this)) {
            AppUtils.setStatusBarDark(this)
        }
    }

    override fun onStop() {
        super.onStop()

        // Set status bar back to light if not in night mode.
        if (!AppUtils.isDarkMode(this)) {
            AppUtils.setStatusBarLight(this)
        }
    }

    // Override to add animation when activity is recreated by "AppCompatDelegate.setDefaultNightMode".
    override fun recreate() {
        finish()
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        startActivity(intent)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    private fun isDialogVisible() : Boolean {
        return this::settingsDialog.isInitialized && settingsDialog.isResumed
    }

    private fun settingDarkMode(key: String) {
        findViewById<View>(R.id.setting_dark_mode).apply {
            // Setup views.
            findViewById<ImageView>(R.id.image_icon).setImageResource(R.drawable.ic_dark_mode)
            findViewById<TextView>(R.id.text_title).text = getString(R.string.dark_mode)
            findViewById<TextView>(R.id.setting_value).text = key
            // Setup click listener for settings dialog.
            setOnClickListener {
                if (!isDialogVisible()) {
                    settingsDialog = SettingsDialog(getString(R.string.dark_mode), viewModel.optionsDarkMode.keys.toList(), key) { selectedKey ->
                        // Handle results from settings dialog.
                        viewModel.setDarkMode(this@SettingsActivity, selectedKey)
                        AppCompatDelegate.setDefaultNightMode(AppSettings.getDarkMode(this@SettingsActivity))
                    }
                    settingsDialog.show(supportFragmentManager, SettingsDialog::class.simpleName)
                }
            }
        }
    }

    private fun settingTextToSpeech(value: Boolean) {
        findViewById<View>(R.id.setting_text_to_speech).apply {
            findViewById<ImageView>(R.id.image_icon).setImageResource(R.drawable.ic_volume)
            findViewById<TextView>(R.id.text_title).text = getString(R.string.text_to_speech)
            findViewById<SwitchCompat>(R.id.setting_switch).isChecked = value
            setOnClickListener {
                findViewById<SwitchCompat>(R.id.setting_switch).isChecked = !value
                viewModel.setTextToSpeech(this@SettingsActivity, !value)
            }
        }
    }
}