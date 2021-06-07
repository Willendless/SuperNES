package com.willendless.nes.frontend

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.willendless.nes.R
import com.willendless.nes.backend.NESDatabaseHelper
import kotlinx.android.synthetic.main.activity_game_intro.*

class GameIntroActivity : AppCompatActivity() {
    companion object {
        private const val GAME_NAME = "game_name"
        private const val GAME_IMAGE_ID = "game_image_id"

        fun actionStart(context: Context, gameName: String, gameImageId: Int) {
            val intent = Intent(context, GameIntroActivity::class.java)
            intent.putExtra(GAME_NAME, gameName)
            intent.putExtra(GAME_IMAGE_ID, gameImageId)
            context.startActivity(intent)
        }
    }

    private val gameInfoItemList = mutableListOf(
        TextImageItem("", R.drawable.game),
        TextImageItem("", R.drawable.calendar),
        TextImageItem("", R.drawable.type)
    )

    private lateinit var gameName: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        gameName = intent.getStringExtra(GAME_NAME)!!
        val gameImageId = intent.getIntExtra(GAME_IMAGE_ID, -1)

        setContentView(R.layout.activity_game_intro)
        setSupportActionBar(normal_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }

        if (gameImageId == -1) {
        } else {
            Glide.with(this).load(gameImageId).into(game_intro_image)
        }

        val dbHelper = NESDatabaseHelper(this, "supernes", 1)
            .writableDatabase

        val cursor = dbHelper.query("game", arrayOf("type", "year", "info"),
            "name=?", arrayOf(gameName), null, null, null)

        gameInfoItemList[0].text = gameName
        if (cursor.moveToFirst()) {
            gameInfoItemList[1].text = cursor.getString(cursor.getColumnIndex("year"))
            gameInfoItemList[2].text = cursor.getString(cursor.getColumnIndex("type"))
            game_intro.text = cursor.getString(cursor.getColumnIndex("info"))
        } else {
            gameInfoItemList[1].text = "未知"
            gameInfoItemList[2].text = "未知"
            game_intro.text = "这个游戏看来需要你自己去探索"
        }
        cursor.close()

        // collect button
        var collected = false
        val collectionCursor = dbHelper.query("collection", null,
            "game_name=?", arrayOf(gameName), null, null, null)
        val img1 = ContextCompat.getDrawable(this, R.drawable.star2)
        img1!!.setBounds(0, 0, 60, 60)
        val img2 = ContextCompat.getDrawable(this, R.drawable.star1)
        img2!!.setBounds(0, 0, 60, 60)

        if (collectionCursor.moveToFirst()) {
            collected = true
            collect.setCompoundDrawables(img1, null, null, null)
        } else {
            collect.setCompoundDrawables(img2, null, null, null)
        }
        collectionCursor.close()
        collect.setOnClickListener {
            collected = !collected
            if (collected) {
                ContentValues().apply {
                    put("game_name", gameName)
                    if (dbHelper.insert("collection", null, this) > 0)
                        collect.setCompoundDrawables(img1, null, null, null)
                }
            } else {
                if (dbHelper.delete("collection", "game_name=?",
                        arrayOf(gameName)) > 0)
                    collect.setCompoundDrawables(img2, null, null, null)
            }
        }

        game_info_recycle_view.setHasFixedSize(true)
        game_info_recycle_view.layoutManager = LinearLayoutManager(this)
        game_info_recycle_view.adapter = GameInfoItemAdapter(this, gameInfoItemList)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }
}