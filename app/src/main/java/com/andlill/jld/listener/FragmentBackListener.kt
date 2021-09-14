package com.andlill.jld.listener

interface FragmentBackListener {
    fun onBackPressed()
    fun requireBackPress(): Boolean
}