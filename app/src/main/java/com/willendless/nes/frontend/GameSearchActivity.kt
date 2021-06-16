package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.willendless.nes.R
import com.willendless.nes.backend.NESDatabaseHelper
import kotlinx.android.synthetic.main.activity_game_collection.*

class GameSearchActivity: AppCompatActivity() {

    companion object {
        fun actionStart(context: Context, searchString: String) {
            val intent = Intent(context, GameSearchActivity::class.java)
            intent.putExtra("searchString", searchString)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_collection)

        listGameTitle.text = "搜索结果"
        setSupportActionBar(normal_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        val gameSearchResultList = arrayListOf<TextImageItem>()

        val searchString = intent.getStringExtra("searchString")!!
        val dbHelper = NESDatabaseHelper(this, "supernes", 1)
            .readableDatabase
        dbHelper.query("game", arrayOf("name", "img_name"),
            "name like ?", arrayOf("$searchString%"),
            null, null, null).use {
                Log.d("query", "Search:$searchString, ${it.count}")
                if (it.moveToFirst()) {
                    do {
                        val name = it.getString(it.getColumnIndex("name"))
                        val imgName = it.getString(it.getColumnIndex("img_name"))
                        gameSearchResultList.add(TextImageItem(name,
                            this.resources.getIdentifier(imgName, "drawable", this.packageName)))
                    } while (it.moveToNext())
                }
        }

        game_collection_recycle_view.apply {
            layoutManager = LinearLayoutManager(this.context)
            adapter = GameSearchItemAdapter(this.context, gameSearchResultList)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}