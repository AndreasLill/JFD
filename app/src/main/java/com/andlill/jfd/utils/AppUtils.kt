package com.andlill.jfd.utils

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.res.Configuration
import android.os.Build
import android.os.Handler
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.os.HandlerCompat
import com.andlill.jfd.R
import com.google.android.material.snackbar.Snackbar

object AppUtils {

    fun copyToClipboard(view: View, text: String) {
        val clipboard = view.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        clipboard.setPrimaryClip(ClipData.newPlainText(text, text))
        Snackbar.make(view, String.format(view.context.getString(R.string.copy_to_clipboard), text), 1500).show()
    }

    @Suppress("DEPRECATION")
    fun vibrate(context: Context) {
        val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (Build.VERSION.SDK_INT >= 26) {
            vibrator.vibrate(VibrationEffect.createOneShot(5, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(5)
        }
    }

    fun showSoftInput(activity: Activity, view: View) {
        val inputMethod = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        view.requestFocus()
        view.postDelayed({ inputMethod.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT) }, 100)
    }

    fun hideSoftInput(activity: Activity, view: View) {
        val inputMethod = activity.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethod.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_IMPLICIT_ONLY)
    }

    fun isDarkMode(activity: Activity): Boolean {
        return activity.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

    fun setStatusBarLight(activity: Activity) {
        val flags = activity.window.decorView.systemUiVisibility
        activity.window.decorView.systemUiVisibility = flags and View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR.inv()
    }

    fun setStatusBarDark(activity: Activity) {
        val flags = activity.window.decorView.systemUiVisibility
        activity.window.decorView.systemUiVisibility = flags or View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
    }

    fun postDelayed(delay: Long, callback: () -> Unit) {
        HandlerCompat.postDelayed(Handler(), { callback() }, null, delay)
    }
}