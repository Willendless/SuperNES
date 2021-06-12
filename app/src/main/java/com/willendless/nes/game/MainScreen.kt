package com.willendless.nes.game

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.Joypads
import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.emulator.util.NESException
import com.willendless.nes.emulator.util.unreachable
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
        val joypadEvents = game.getJoypadEvents()
        Log.d("touchEvents len", "${joypadEvents.size}")

        Joypads.reset()
        for (e in joypadEvents) {
            val value = when (e.type) {
                Input.JoypadEvent.UP -> Joypads.setStatus(Joypads.JoypadButtonFlag.UP, true)
                Input.JoypadEvent.DOWN -> Joypads.setStatus(Joypads.JoypadButtonFlag.DOWN, true)
                Input.JoypadEvent.LEFT -> Joypads.setStatus(Joypads.JoypadButtonFlag.LEFT, true)
                Input.JoypadEvent.RIGHT -> Joypads.setStatus(Joypads.JoypadButtonFlag.RIGHT, true)
                Input.JoypadEvent.A -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_A, true)
                Input.JoypadEvent.B -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_B, true)
                Input.JoypadEvent.SELECT -> Joypads.setStatus(Joypads.JoypadButtonFlag.SELECT, true)
                Input.JoypadEvent.START -> Joypads.setStatus(Joypads.JoypadButtonFlag.START, true)
                else -> {}
            }
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