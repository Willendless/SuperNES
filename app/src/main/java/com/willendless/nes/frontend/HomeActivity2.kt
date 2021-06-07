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
import androidx.recyclerview.widget.GridLayoutManager
import com.willendless.nes.R
import com.willendless.nes.backend.NESDatabaseHelper
import kotlinx.android.synthetic.main.activity_home2.*
import org.w3c.dom.Text

class HomeActivity2 : AppCompatActivity(), SearchView.OnQueryTextListener {

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, HomeActivity2::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home2)
        setSupportActionBar(search_bar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setHomeAsUpIndicator(R.drawable.menu)
        }
        favorite_button.setOnClickListener {
            GameCollectionActivity.actionStart(this)
        }

        val dbHelper = NESDatabaseHelper(this, "supernes", 1)
            .readableDatabase
        val cursor = dbHelper.query("game", arrayOf("name", "img_name"),
            null, null, null, null, "id")
        val gameList = mutableListOf<TextImageItem>()
        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex("name"))
                val imgName = cursor.getString(cursor.getColumnIndex("img_name"))
                gameList.add(TextImageItem(name, this.resources.
                    getIdentifier(imgName, "drawable", this.packageName)))
            } while (cursor.moveToNext());
        }
        game_recycle_view.let {
            it.layoutManager = GridLayoutManager(this, 2)
            it.adapter = GameAdapter(this, gameList)
        }
        cursor.close()
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
        TODO("Not yet implemented")
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        TODO("Not yet implemented")
    }

}