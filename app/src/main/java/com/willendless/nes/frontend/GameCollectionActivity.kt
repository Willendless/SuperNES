package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.willendless.nes.R
import kotlinx.android.synthetic.main.activity_game_collection.*

class GameCollectionActivity : AppCompatActivity() {

    companion object {
        fun actionStart(context: Context) {
            val intent = Intent(context, GameCollectionActivity::class.java)
            context.startActivity(intent)
        }
    }

    private val gameCollectionList = mutableListOf(
        TextImageItem("super mario", R.drawable.super_mario),
        TextImageItem("super mario", R.drawable.super_mario),
        TextImageItem("super mario", R.drawable.super_mario)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_collection)

        setSupportActionBar(normal_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        // search for collection list
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