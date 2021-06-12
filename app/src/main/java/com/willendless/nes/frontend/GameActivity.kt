package com.willendless.nes.frontend

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import com.willendless.nes.R
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Pool
import com.willendless.nes.framework.Screen
import com.willendless.nes.framework.impl.AndroidGame
import com.willendless.nes.game.LoadingScreen
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity: AndroidGame(), View.OnClickListener {
    private val joypadEventPool = Pool<Input.JoypadEvent>(100) { Input.JoypadEvent()  }
    private val joypadEventsBuffer = ArrayList<Input.JoypadEvent>()
    private val joypadEvents = ArrayList<Input.JoypadEvent>()

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

        val joypadButtons = arrayOf(
            pad_a,
            pad_b,
            pad_start,
            pad_select,
            pad_up,
            pad_down,
            pad_left,
            pad_right
        )

        for (b in joypadButtons) {
            b.setOnClickListener(this)
        }
    }

    override fun getStartScreen(): Screen =
        LoadingScreen(intent.getStringExtra("filepath")!!, this)

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    override fun onClick(v: View?) {
        Log.d("click!", "${v?.id}")
        if (v != null) {
            synchronized(this) {
                joypadEventPool.newObject().let {
                    it.type = when (v.id) {
                        R.id.pad_a -> Input.JoypadEvent.A
                        R.id.pad_b -> Input.JoypadEvent.B
                        R.id.pad_select -> Input.JoypadEvent.SELECT
                        R.id.pad_start -> Input.JoypadEvent.START
                        R.id.pad_up -> Input.JoypadEvent.UP
                        R.id.pad_down -> Input.JoypadEvent.DOWN
                        R.id.pad_left -> Input.JoypadEvent.LEFT
                        R.id.pad_right -> Input.JoypadEvent.RIGHT
                        else -> unreachable("Unknown key")
                    }
                    joypadEventsBuffer.add(it)
                }
            }
        }
    }

    override fun getJoypadEvents(): List<Input.JoypadEvent> {
        synchronized(this) {
            val len = joypadEvents.size
            for (i in 0 until len) {
                joypadEventPool.free(joypadEvents[i])
            }
            joypadEvents.clear()
            joypadEvents.addAll(joypadEventsBuffer)
            joypadEventsBuffer.clear()
            return joypadEvents
        }
    }
}