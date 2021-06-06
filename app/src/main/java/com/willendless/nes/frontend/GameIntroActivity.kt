package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.willendless.nes.R
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
        GameInfoItem("", R.drawable.game),
        GameInfoItem("", R.drawable.calendar)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val gameName = intent.getStringExtra(GAME_NAME)
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

        gameInfoItemList[0].text = gameName!!
        gameInfoItemList[1].text = "1990"

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