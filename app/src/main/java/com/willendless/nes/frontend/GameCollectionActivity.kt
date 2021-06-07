package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.willendless.nes.R
import com.willendless.nes.backend.NESDatabaseHelper
import kotlinx.android.synthetic.main.activity_game_collection.*

class GameCollectionActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, GameCollectionActivity::class.java)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_collection)

        setSupportActionBar(normal_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        // search for collection list
        val gameCollectionList = mutableListOf<TextImageItem>()
        val dbHelper = NESDatabaseHelper(this, "supernes", 1)
            .readableDatabase
        val cursor = dbHelper.query("collection", arrayOf("game_name"),
            null, null, null, null, null)

        Log.d("collect count", "${cursor.count}")

        if (cursor.moveToFirst()) {
            do {
                val name = cursor.getString(cursor.getColumnIndex("game_name"))
                val cursor2 = dbHelper.query("game", arrayOf("name", "img_name"),
                    "name=?", arrayOf(name), null, null, "id")
                cursor2.moveToFirst()
                val imgName = cursor2.getString(cursor2.getColumnIndex("img_name"))
                gameCollectionList.add(TextImageItem(name,
                    this.resources.getIdentifier(imgName, "drawable", this.packageName)))
                cursor2.close()
            } while (cursor.moveToNext())
        }
        cursor.close()

        game_collection_recycle_view.let {
            it.layoutManager = LinearLayoutManager(this)
            it.adapter = GameCollectionItemAdapter(this, gameCollectionList)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

}