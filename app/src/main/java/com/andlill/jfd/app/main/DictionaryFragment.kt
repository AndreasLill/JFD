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

class DictionaryFragment : Fragment(R.layout.fragment_dictionary) {

    companion object {
        private const val STATE_DATA_DICTIONARY = "com.andlill.jld.DictionaryFragment.State.DataDictionary"
        private const val STATE_RECYCLER_DICTIONARY = "com.andlill.jld.DictionaryFragment.State.RecyclerDictionary"
        private val savedState: Bundle = Bundle()
    }

    private lateinit var viewModel: DictionaryFragmentViewModel
    private lateinit var dictionaryAdapter: DictionaryAdapter
    private lateinit var dictionaryRecycler: RecyclerView
    private lateinit var textHint: TextView

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(this)[DictionaryFragmentViewModel::class.java]
        viewModel.getDictionaryResult().observe(viewLifecycleOwner, { dictionaryResult ->
            textHint.visibility = View.INVISIBLE
            requireView().findViewById<RecyclerView>(R.id.recycler_dictionary)?.scrollToPosition(0)
            dictionaryAdapter.update(dictionaryResult)
        })

        this.initializeUI()
        this.initializeSavedState()
    }

    private fun initializeUI() {
        val view = requireView()
        textHint = view.findViewById(R.id.text_hint)
        dictionaryAdapter = DictionaryAdapter { entry ->
            val intent = Intent(requireContext(), DictionaryDetailsActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.putExtra(DictionaryDetailsActivity.ARGUMENT_ENTRY_ID, entry.id)
            startActivity(intent)
        }
        dictionaryRecycler = view.findViewById<RecyclerView>(R.id.recycler_dictionary).apply {
            layoutManager = LinearLayoutManager(requireContext())
            addItemDecoration(DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL))
            setHasFixedSize(true)
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
            if (value.isNotEmpty()) {
                textHint.visibility = View.INVISIBLE
            }
            dictionaryAdapter.update(value)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()

        savedState.putSerializable(STATE_DATA_DICTIONARY, dictionaryAdapter.dataSet())
        savedState.putParcelable(STATE_RECYCLER_DICTIONARY, dictionaryRecycler.layoutManager?.onSaveInstanceState())
    }

    fun searchDictionary(query: String, callback: (Int) -> Unit) {
        viewModel.searchDictionary(query) { resultCount ->
            callback(resultCount)
        }
    }
}