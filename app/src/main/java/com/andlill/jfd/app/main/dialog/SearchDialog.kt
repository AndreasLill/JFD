package com.andlill.jfd.app.main.dialog

import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.main.MainActivityViewModel
import com.andlill.jfd.app.main.adapter.SearchHistoryAdapter
import com.andlill.jfd.utils.AppUtils

class SearchDialog(private val viewModel: MainActivityViewModel, private val callback: (String) -> Unit) : DialogFragment() {

    private lateinit var input: EditText
    private lateinit var buttonBack: ImageButton
    private lateinit var buttonInputClear: ImageButton
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val view = inflater.inflate(R.layout.dialog_search, container, false)

        buttonBack = view.findViewById<ImageButton>(R.id.button_back).apply { setOnClickListener { closeDialog() } }
        buttonInputClear = view.findViewById<ImageButton>(R.id.button_input_clear).apply { setOnClickListener { input.setText("") } }

        // Setup edit text input.
        input = view.findViewById<EditText>(R.id.edit_text_search).apply {
            setOnEditorActionListener { _, action, event ->
                if (action == EditorInfo.IME_ACTION_GO || action == EditorInfo.IME_ACTION_DONE || event.keyCode == KeyEvent.KEYCODE_ENTER) {
                    AppUtils.postDelayed(100) {
                        search(input.text.toString())
                    }
                }
                false
            }
            addTextChangedListener {
                when {
                    it.isNullOrEmpty() -> buttonInputClear.visibility = View.INVISIBLE
                    it.isNotEmpty()-> buttonInputClear.visibility = View.VISIBLE
                }
            }
        }

        // Setup search history adapter and recycler view.
        searchHistoryAdapter = SearchHistoryAdapter { action, searchHistory ->
            when (action) {
                SearchHistoryAdapter.Action.Select -> {
                    AppUtils.postDelayed(100) {
                        search(searchHistory.value)
                    }
                }
                SearchHistoryAdapter.Action.Delete -> {
                    viewModel.deleteSearchHistory(requireContext(), searchHistory)
                }
            }
        }

        view.findViewById<RecyclerView>(R.id.recycler_history).apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = searchHistoryAdapter
            setHasFixedSize(true)
        }

        // Setup view model observe.
        viewModel.getSearchHistory().observe(this, { searchHistory ->
            searchHistoryAdapter.submitList(searchHistory.toList())
        })

        return view
    }

    private fun search(query: String) {
        // Callback to activity and close.
        callback(query.trim())
        closeDialog()
    }

    private fun closeDialog() {
        // Pop the back stack to close dialog.
        requireActivity().supportFragmentManager.popBackStack()
    }

    override fun onResume() {
        super.onResume()
        AppUtils.showSoftInput(requireActivity(), input)

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

        AppUtils.hideSoftInput(requireActivity(), input)
        super.onStop()
    }
}