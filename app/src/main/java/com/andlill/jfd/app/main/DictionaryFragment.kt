package com.andlill.jfd.app.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jfd.R
import com.andlill.jfd.app.dictionarydetails.DictionaryDetailsActivity
import com.andlill.jfd.app.main.adapter.DictionaryAdapter
import com.andlill.jfd.model.DictionaryEntry
import com.andlill.jfd.utils.AppUtils
import dev.esnault.wanakana.core.Wanakana

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    companion object {
        private const val STATE_DATA_DICTIONARY = "com.andlill.jld.DictionaryFragment.State.DataDictionary"
        private const val STATE_RECYCLER_DICTIONARY = "com.andlill.jld.DictionaryFragment.State.RecyclerDictionary"
        private const val STATE_TEXT_QUERY = "com.andlill.jld.DictionaryFragment.State.TextQuery"
        private const val STATE_TEXT_RESULT = "com.andlill.jld.DictionaryFragment.State.TextResult"
        private val savedState: Bundle = Bundle()
    }

    private lateinit var viewModel: DictionaryFragmentViewModel
    private lateinit var dictionaryAdapter: DictionaryAdapter
    private lateinit var dictionaryRecycler: RecyclerView
    private lateinit var textHint: TextView
    private lateinit var layoutSuggestion: View
    private lateinit var textSuggestion: TextView
    private lateinit var layoutResults: View
    private lateinit var textQuery: TextView
    private lateinit var textResult: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DictionaryFragmentViewModel::class.java]
        viewModel.getDictionaryResult().observe(viewLifecycleOwner) { dictionaryResult ->
            requireView().findViewById<RecyclerView>(R.id.recycler_dictionary)?.scrollToPosition(0)
            dictionaryAdapter.update(dictionaryResult)
        }

        this.initializeUI()
        this.initializeSavedState()
    }

    private fun initializeUI() {
        val view = requireView()
        textHint = view.findViewById(R.id.text_hint)
        layoutResults = view.findViewById(R.id.layout_results)
        textQuery = view.findViewById(R.id.text_results_query)
        textResult = view.findViewById(R.id.text_results_count)
        textSuggestion = view.findViewById(R.id.text_suggestion)
        layoutSuggestion = view.findViewById<View?>(R.id.layout_suggestion).apply {
            setOnClickListener {
                AppUtils.postDelayed(100) {
                    layoutSuggestion.visibility = View.GONE
                    viewModel.searchDictionary(textSuggestion.text.toString()) { count ->
                        layoutResults.visibility = View.VISIBLE
                        textQuery.text = textSuggestion.text.toString()
                        textResult.text = getString(R.string.dictionary_results_count, count.toString())
                    }
                }
            }
        }
        dictionaryAdapter = DictionaryAdapter { entry ->
            val intent = Intent(requireContext(), DictionaryDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(DictionaryDetailsActivity.ARGUMENT_ENTRY_ID, entry.id)
            startActivity(intent)
        }
        dictionaryRecycler = view.findViewById<RecyclerView>(R.id.recycler_dictionary).apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            adapter = dictionaryAdapter
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun initializeSavedState() {
        if (savedState.containsKey(STATE_RECYCLER_DICTIONARY)) {
            dictionaryRecycler.layoutManager?.onRestoreInstanceState(savedState.getParcelable(STATE_RECYCLER_DICTIONARY))
        }
        if (savedState.containsKey(STATE_DATA_DICTIONARY)) {
            val value = savedState.getSerializable(STATE_DATA_DICTIONARY) as ArrayList<DictionaryEntry>
            dictionaryAdapter.update(value)
        }
        if (savedState.containsKey(STATE_TEXT_QUERY)) {
            textQuery.text = savedState.getString(STATE_TEXT_QUERY, "")
        }
        if (savedState.containsKey(STATE_TEXT_RESULT)) {
            textResult.text = savedState.getString(STATE_TEXT_RESULT, "")

            if (textResult.text.isNotEmpty()) {
                layoutResults.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        savedState.putSerializable(STATE_DATA_DICTIONARY, dictionaryAdapter.dataSet())
        savedState.putParcelable(STATE_RECYCLER_DICTIONARY, dictionaryRecycler.layoutManager?.onSaveInstanceState())
        savedState.putString(STATE_TEXT_QUERY, textQuery.text.toString())
        savedState.putString(STATE_TEXT_RESULT, textResult.text.toString())
    }

    fun searchDictionary(query: String, callback: () -> Unit) {
        viewModel.searchDictionary(query) { count ->
            textHint.visibility = View.GONE
            layoutResults.visibility = View.VISIBLE
            textQuery.text = query
            textResult.text = getString(R.string.dictionary_results_count, count.toString())
            textSuggestion.text = ""
            layoutSuggestion.visibility = View.GONE

            // Set suggestion hint above results.
            if (Wanakana.isRomaji(query)) {
                val value = Wanakana.toHiragana(query)
                if (Wanakana.isHiragana(value)) {
                    textSuggestion.text = value
                    layoutSuggestion.visibility = View.VISIBLE
                }
            }
            callback()
        }
    }
}