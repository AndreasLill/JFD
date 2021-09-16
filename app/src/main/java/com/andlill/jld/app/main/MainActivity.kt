package com.andlill.jld.app.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.andlill.jld.BuildConfig
import com.andlill.jld.R
import com.andlill.jld.app.main.adapter.SearchHistoryAdapter
import com.andlill.jld.app.settings.SettingsActivity
import com.andlill.jld.io.repository.*
import com.andlill.jld.utils.AppPreferences
import com.google.android.material.color.MaterialColors
import com.google.android.material.navigation.NavigationView
import com.google.android.material.progressindicator.LinearProgressIndicator
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import kotlinx.coroutines.*
import java.util.*

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, TabLayout.OnTabSelectedListener {

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var preferences: SharedPreferences
    private lateinit var progressBar: LinearProgressIndicator
    private lateinit var searchViewMenuItem: MenuItem
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

    private lateinit var navigationDrawer: NavigationView
    private lateinit var tabs: Array<Int>
    private lateinit var viewPager: ViewPager2
    private lateinit var pagerAdapter: PagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Get shared preferences.
        preferences = getSharedPreferences(AppPreferences.PREFERENCES, Context.MODE_PRIVATE)

        // Check night mode.
        when (preferences.getString(AppPreferences.KEY_DARK_MODE, AppPreferences.DarkModeOptions[0])) {
            AppPreferences.DarkModeOptions[0] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppPreferences.DarkModeOptions[1] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            AppPreferences.DarkModeOptions[2] -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)

        this.initializeUI()
        this.initializeTabs()
    }

    private fun initializeUI() {
        // Setup the toolbar drawer toggle button.
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        toolbar.subtitle = getString(R.string.dictionary_ready)
        toolbar.setOnClickListener { searchViewMenuItem.expandActionView() }
        setSupportActionBar(toolbar)

        // Progress bar.
        progressBar = findViewById(R.id.progress_bar)

        // Setup drawer layout.
        val drawerLayout = findViewById<DrawerLayout>(R.id.layout_drawer)
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open, R.string.close)
        drawerToggle.isDrawerIndicatorEnabled = true
        drawerLayout.addDrawerListener(drawerToggle)
        drawerToggle.syncState()

        // Setup navigation.
        navigationDrawer = findViewById(R.id.navigation_drawer)
        navigationDrawer.setNavigationItemSelectedListener(this)

        // Set current build version to drawer header text.
        val versionTextView = navigationDrawer.getHeaderView(0).findViewById<TextView>(R.id.text_title_version)
        versionTextView.text = BuildConfig.VERSION_NAME

        searchHistoryAdapter = SearchHistoryAdapter { action, searchHistory ->
            when (action) {
                SearchHistoryAdapter.CallbackAction.Select -> searchDictionary(searchHistory.value)
                SearchHistoryAdapter.CallbackAction.Delete -> viewModel.deleteSearchHistory(this@MainActivity, searchHistory)
            }
        }

        findViewById<RecyclerView>(R.id.recycler_history).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            addItemDecoration(DividerItemDecoration(this@MainActivity, DividerItemDecoration.VERTICAL))
            adapter = searchHistoryAdapter
        }

        viewModel.getSearchHistory().observe(this, { searchHistory ->
            searchHistoryAdapter.submitList(ArrayList(searchHistory))
        })
    }

    private fun initializeTabs() {
        tabs = arrayOf(R.string.menu_item_dictionary, R.string.menu_item_collections)

        // Setup view pager.
        pagerAdapter = PagerAdapter(this)
        viewPager = findViewById(R.id.pager)
        viewPager.adapter = pagerAdapter

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

    override fun onStart() {
        super.onStart()
        // Initialize view model on start in case of external updates.
        viewModel.initialize(this)
    }

    override fun onResume() {
        super.onResume()

        // Check if assets require reloading into memory.
        if (viewModel.requireReloadAssets()) {
            // Update progress bar and toolbar subtitle.
            this.showProgressBar()
            supportActionBar?.subtitle = getString(R.string.dictionary_loading)

            // Load required assets and callback when complete.
            viewModel.loadAssets(assets) {
                this.hideProgressBar()
                supportActionBar?.subtitle = getString(R.string.dictionary_ready)
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_item_dictionary -> viewPager.setCurrentItem(0, true)
            R.id.menu_item_collections -> viewPager.setCurrentItem(1, true)
            R.id.menu_item_settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(intent)
            }
        }

        findViewById<DrawerLayout>(R.id.layout_drawer).closeDrawer(GravityCompat.START)
        return true
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        if (tab != null) {
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
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_activity_main, menu)

        // Configure search bar.
        searchViewMenuItem = menu?.findItem(R.id.menu_item_search) as MenuItem

        // Setup expand and collapse listeners.
        searchViewMenuItem.setOnActionExpandListener(object: MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                findViewById<View>(R.id.recycler_history).visibility = View.VISIBLE
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                findViewById<View>(R.id.recycler_history).visibility = View.GONE
                return true
            }
        })

        val searchView = searchViewMenuItem.actionView as SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        searchView.setBackgroundResource(R.drawable.background_search_view)

        val searchEditText = searchView.findViewById<EditText>(androidx.appcompat.R.id.search_src_text)
        searchEditText.setHintTextColor(MaterialColors.getColor(searchView, R.attr.colorHint))
        searchEditText.setTextColor(MaterialColors.getColor(searchView, R.attr.colorOnSurface))
        searchEditText.textSize = 16f

        val searchPlate = searchView.findViewById<View>(androidx.appcompat.R.id.search_plate)
        searchPlate.setBackgroundColor(Color.TRANSPARENT)

        val searchCloseIcon = searchView.findViewById<ImageView>(androidx.appcompat.R.id.search_close_btn)
        searchCloseIcon.setColorFilter(MaterialColors.getColor(searchView, R.attr.colorOnSurface))

        return true
    }

    override fun onBackPressed() {
        /* Cast to FragmentBackListener or null. */
        val drawerLayout = findViewById<DrawerLayout>(R.id.layout_drawer)

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawer(GravityCompat.START)
        else
            super.onBackPressed()
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleIntent(intent)
    }

    fun isDictionaryReady(): Boolean {
        return !progressBar.isIndeterminate
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
        progressBar.apply {
            visibility = View.INVISIBLE
            isIndeterminate = true
            visibility = View.VISIBLE
        }
    }

    private fun hideProgressBar() {
        progressBar.apply {
            visibility = View.INVISIBLE
            isIndeterminate = false
            visibility = View.GONE
        }
    }

    private fun handleIntent(intent: Intent) {
        if (intent.action == Intent.ACTION_SEARCH) {
            val query = intent.getStringExtra(SearchManager.QUERY)

            if (!query.isNullOrEmpty())
                searchDictionary(query)
        }
    }

    private fun searchDictionary(query: String) {
        // Collapse the search action view.
        searchViewMenuItem.collapseActionView()

        // Check if dictionary is ready.
        if (!isDictionaryReady())
            return

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