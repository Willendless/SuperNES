package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.LoadingScreen
import kotlinx.android.synthetic.main.activity_game.*
import kotlinx.android.synthetic.main.game_item.*
import kotlin.system.exitProcess

class GameActivity: AndroidGame() {

    companion object {
        fun actionStart(context: Context, name: String, filepath: String) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("name", name)
            intent.putExtra("filepath", filepath)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val gamename = intent.getStringExtra("name")!!

        setSupportActionBar(normal_toolbar)
        supportActionBar?.let {
            it.setDisplayHomeAsUpEnabled(true)
            it.setDisplayShowTitleEnabled(false)
        }
        game_title.text = gamename

    }

    override fun getStartScreen(): Screen =
        LoadingScreen(intent.getStringExtra("filepath")!!, this)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}