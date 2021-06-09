package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.LoadingScreen

class GameActivity: AndroidGame() {

    companion object {
        fun actionStart(context: Context, filepath: String) {
            val intent = Intent(context, GameActivity::class.java)
            intent.putExtra("filepath", filepath)
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getStartScreen(): Screen =
        LoadingScreen(intent.getStringExtra("filepath")!!, this)
}