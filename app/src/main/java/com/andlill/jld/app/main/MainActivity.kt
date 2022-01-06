package com.andlill.jld.app.main

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.andlill.jld.R
import com.andlill.jld.app.main.dialog.AboutDialog
import com.andlill.jld.app.main.dialog.SearchDialog
import com.andlill.jld.app.settings.SettingsActivity
import com.andlill.jld.io.repository.*
import com.andlill.jld.utils.AppSettings
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var tabs: Array<Int>
    private lateinit var navigationDrawer: NavigationView
    private lateinit var viewPager: ViewPager2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.initialize(this)

        this.initializeUI()
        this.initializeTabs()
    }

    private fun initializeUI() {
        // Setup dark mode.
        AppCompatDelegate.setDefaultNightMode(AppSettings.getDarkMode(this))

        // Setup the toolbar.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.setOnClickListener { openSearchDialog() }
        setSupportActionBar(toolbar)

        // Setup drawer layout.
        val drawerLayout = findViewById<DrawerLayout>(R.id.layout_drawer)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerLayout.setStatusBarBackgroundColor(MaterialColors.getColor(drawerLayout, R.attr.colorPrimary))
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Setup navigation.
        navigationDrawer = findViewById(R.id.navigation_drawer)
        navigationDrawer.setNavigationItemSelectedListener(this)
    }

    private fun initializeTabs() {
        tabs = arrayOf(R.string.menu_item_dictionary, R.string.menu_item_collections)

        // Setup view pager.
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = PagerAdapter(this)

        // Setup tab layout.
        val tabLayout = findViewById<TabLayout>(R.id.layout_tab).apply {
            addOnTabSelectedListener(this@MainActivity)
        }
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            when (tabs[position]) {
                R.string.menu_item_dictionary -> {
                    tab.setIcon(R.drawable.tab_ic_search)
                    tab.setText(R.string.menu_item_dictionary)
                }
                R.string.menu_item_collections -> {
                    tab.setIcon(R.drawable.tab_ic_collections)
                    tab.setText(R.string.menu_item_collections)
                }
            }
        }.attach()
    }

    override fun onResume() {
        super.onResume()

        // Check if assets require reloading into memory.
        if (!viewModel.isDictionaryReady()) {
            this.showProgressBar()

            // Load required assets and callback when complete.
            viewModel.loadAssets(assets) {
                this.hideProgressBar()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_search -> openSearchDialog()
        }
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val drawer = findViewById<DrawerLayout>(R.id.layout_drawer)

        when (item.itemId) {
            R.id.menu_item_dictionary -> {
                viewPager.setCurrentItem(0, true)
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.menu_item_collections -> {
                viewPager.setCurrentItem(1, true)
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.menu_item_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
                drawer.closeDrawer(GravityCompat.START)
            }
            R.id.menu_item_about -> {
                AboutDialog().show(supportFragmentManager, AboutDialog::class.simpleName)
            }
        }

        return true
    }

    override fun onTabSelected(tab: TabLayout.Tab) {
        when (tabs[tab.position]) {
            R.string.menu_item_dictionary -> {
                supportActionBar?.title = getString(R.string.menu_item_dictionary)
                navigationDrawer.menu.findItem(R.id.menu_item_dictionary).isChecked = true
            }
            R.string.menu_item_collections -> {
                supportActionBar?.title = getString(R.string.menu_item_collections)
                navigationDrawer.menu.findItem(R.id.menu_item_collections).isChecked = true
            }
        }
    }

    override fun onTabUnselected(tab: TabLayout.Tab) {
    }

    override fun onTabReselected(tab: TabLayout.Tab) {
    }

    override fun onBackPressed() {
        val drawerLayout = findViewById<DrawerLayout>(R.id.layout_drawer)

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    private fun openSearchDialog() {
        if (!viewModel.isDictionaryReady()) {
            Snackbar.make(findViewById(R.id.layout_root), getString(R.string.dictionary_wait), 1500).show()
            return
        }

        // Open search dialog and get search query from callback.
        val dialog = SearchDialog(viewModel) { query ->
            if (query.isNotEmpty())
                searchDictionary(query)
        }

        // Start transaction to open search dialog.
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.add(R.id.layout_drawer, dialog).addToBackStack(null).commit()
    }

    inner class PagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int {
            return tabs.size
        }

        override fun createFragment(position: Int): Fragment {
            var fragment = Fragment()

            when (tabs[position]) {
                R.string.menu_item_dictionary -> {
                    fragment = DictionaryFragment()
                }
                R.string.menu_item_collections -> {
                    fragment = CollectionFragment()
                }
            }

            return fragment
        }
    }

    private fun showProgressBar() {
        findViewById<LinearProgressIndicator>(R.id.progress_bar).apply {
            visibility = View.INVISIBLE
            isIndeterminate = true
            visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        findViewById<LinearProgressIndicator>(R.id.progress_bar).apply {
            visibility = View.INVISIBLE
            isIndeterminate = false
            visibility = View.GONE
        }
    }

    private fun searchDictionary(query: String) {
        // Scroll to dictionary fragment in view pager.
        viewPager.setCurrentItem(0, false)
        val fragment = supportFragmentManager.findFragmentByTag("f" + viewPager.currentItem) as DictionaryFragment

        this.showProgressBar()

        // Call fragment to search dictionary.
        fragment.searchDictionary(query) {
            this.hideProgressBar()
        }

        // Update search history.
        viewModel.updateSearchHistory(this, query)
    }
}