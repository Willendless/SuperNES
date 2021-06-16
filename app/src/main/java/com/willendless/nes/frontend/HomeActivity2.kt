package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.google.android.material.navigation.NavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_home2.*

class HomeActivity2 : AppCompatActivity(), SearchView.OnQueryTextListener,
    NavigationView.OnNavigationItemSelectedListener {

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, HomeActivity2::class.java)
            context.startActivity(intent)
        }
    }

    private val fragmentsTabTitles = arrayOf(
        "全部",
        "街机",
        "射击",
        "迷宫",
        "解谜"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)
        setSupportActionBar(search_bar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.menu)
            it.setDisplayShowTitleEnabled(false)
        }
        favorite_button.setOnClickListener {
            GameCollectionActivity.actionStart(this)
        }

        // navigation view
        nav_view.setCheckedItem(R.id.nav_collection)
        nav_view.setNavigationItemSelectedListener(this)

        viewPager.adapter = GamePageAdapter(this)

        TabLayoutMediator(homeTab, viewPager) { tab: TabLayout.Tab, i: Int ->
            tab.text = fragmentsTabTitles[i]
        }.attach()
    }

    inner class GamePageAdapter(fa: FragmentActivity)
        : FragmentStateAdapter(fa) {
        override fun getItemCount(): Int = fragmentsTabTitles.size

        override fun createFragment(position: Int): Fragment =
            HomeGameFragment(fragmentsTabTitles[position])
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        if (menu != null) {
            menuInflater.inflate(R.menu.home_toolbar, menu)
            val menuItem = menu.findItem(R.id.search_icon)
            val listener = this
            (menuItem?.actionView as SearchView).apply {
                queryHint = "搜索你想玩的游戏"
                setOnQueryTextListener(listener)
                return true
            }
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> drawer_layout.openDrawer(GravityCompat.START)
            R.id.version -> Toast.makeText(this, "SuperNES v0.1 -- by JR & QY",
                Toast.LENGTH_SHORT).show()
        }
        return true
    }

    override fun onQueryTextSubmit(query: String?): Boolean {
        if (query != null) {
            GameSearchActivity.actionStart(this, query)
        } else {
            Toast.makeText(this, "请输入你想查找的游戏名", Toast.LENGTH_SHORT)
        }
        return true
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_collection -> GameCollectionActivity.actionStart(this)
            R.id.nav_open_nes_dev -> WebViewActivity.actionStart(this,
                "https://nesdev.com/")
            R.id.nav_open_nes_world -> WebViewActivity.actionStart(this,
                "https://www.nesworld.com/")
        }
        return true
    }
}