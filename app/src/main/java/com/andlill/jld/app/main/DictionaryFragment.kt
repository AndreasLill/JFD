package com.andlill.jld.app.main

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.andlill.jld.R
import com.andlill.jld.app.main.adapter.DictionaryAdapter
import com.andlill.jld.app.dictionarydetails.DictionaryDetailsActivity
import com.andlill.jld.model.DictionaryEntry

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    companion object {
        private const val STATE_DATA_DICTIONARY = "com.andlill.jld.DictionaryFragment.StateDataDictionary"
        private const val STATE_RECYCLER_DICTIONARY = "com.andlill.jld.DictionaryFragment.StateRecyclerDictionary"
        private var savedState: Bundle = Bundle()
    }

    private lateinit var viewModel: DictionaryFragmentViewModel
    private lateinit var dictionaryAdapter: DictionaryAdapter
    private lateinit var dictionaryRecycler: RecyclerView

    private var query = ""

    @Suppress("UNCHECKED_CAST")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this).get(DictionaryFragmentViewModel::class.java)

        dictionaryAdapter = DictionaryAdapter { entry ->
            val intent = Intent(requireContext(), DictionaryDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(DictionaryDetailsActivity.ARGUMENT_ENTRY_ID, entry.id)
            startActivity(intent)
        }

        // Setup dictionary recycler view.
        dictionaryRecycler = view.findViewById<RecyclerView>(R.id.recycler_dictionary).apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
            adapter = dictionaryAdapter
        }

        // Handle saved states.
        if (savedState.containsKey(STATE_RECYCLER_DICTIONARY)) {
            dictionaryRecycler.layoutManager?.onRestoreInstanceState(savedState.getParcelable(STATE_RECYCLER_DICTIONARY))
        }
        if (savedState.containsKey(STATE_DATA_DICTIONARY)) {
            val value = savedState.getSerializable(STATE_DATA_DICTIONARY) as ArrayList<DictionaryEntry>
            if (value.isNotEmpty()) {
                view.findViewById<View>(R.id.text_hint).visibility = View.INVISIBLE
            }
            dictionaryAdapter.update(value)
        }

        viewModel.getDictionaryResult().observe(viewLifecycleOwner, { dictionaryResult ->
            view.findViewById<View>(R.id.text_hint).visibility = View.INVISIBLE
            when {
                dictionaryResult.isEmpty() -> view.findViewById<TextView>(R.id.text_result).text = String.format(getString(R.string.dictionary_no_results), query)
                dictionaryResult.isNotEmpty() -> view.findViewById<TextView>(R.id.text_result).text = ""
            }

            requireView().findViewById<RecyclerView>(R.id.recycler_dictionary)?.scrollToPosition(0)
            dictionaryAdapter.update(dictionaryResult)
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()

        savedState = Bundle()
        savedState.putSerializable(STATE_DATA_DICTIONARY, dictionaryAdapter.dataSet())
        savedState.putParcelable(STATE_RECYCLER_DICTIONARY, dictionaryRecycler.layoutManager?.onSaveInstanceState())
    }

    fun searchDictionary(query: String, callback: () -> Unit) {
        // Store the latest query.
        this.query = query
        // Perform dictionary search.
        viewModel.searchDictionary(query) {
            callback()
        }
    }
}