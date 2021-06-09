package com.willendless.nes.game

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.Joypads
import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.emulator.util.NESException
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Screen
import kotlin.system.exitProcess

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class MainScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float): Boolean {
        val bottom = 1920
        val right = 1080
        val touchEvents = game.getInput().getTouchEvents()
        Log.d("touchEvents len", "${touchEvents.size}")
        for (e in touchEvents) {
            val value = when (e.type) {
                Input.TouchEvent.TOUCH_DOWN -> true
                Input.TouchEvent.TOUCH_UP -> false
                else -> true
            }
            Joypads.setStatus(Joypads.JoypadButtonFlag.START, value)
//            when {
//                e.x in (right / 4 * 3..right) && e.y in (bottom / 4 * 3.. bottom / 8 * 7) ->
//                    Joypads.setStatus(Joypads.JoypadButtonFlag.START, value)
//            }
            Log.d("", "${e.type}, $value, (${e.x}, ${e.y}),  mem val ${Joypads.buttonStatus}")
        }

        try {
            return CPU.run(1000)
        } catch (e: NESException) {
            Log.e("CPU", e.msg)
            exitProcess(-1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun present(deltaTime: Float) {
        PPU.render(game.getGraphics())
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        NESBus.clear()
    }
}