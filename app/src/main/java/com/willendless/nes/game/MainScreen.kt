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
        val joypadEvents = game.getJoypadEvents()

        for (e in joypadEvents) {
            when (e.type) {
                Input.JoypadEvent.UP -> Joypads.setStatus(Joypads.JoypadButtonFlag.UP, e.padDown)
                Input.JoypadEvent.DOWN -> Joypads.setStatus(Joypads.JoypadButtonFlag.DOWN, e.padDown)
                Input.JoypadEvent.LEFT -> Joypads.setStatus(Joypads.JoypadButtonFlag.LEFT, e.padDown)
                Input.JoypadEvent.RIGHT -> Joypads.setStatus(Joypads.JoypadButtonFlag.RIGHT, e.padDown)
                Input.JoypadEvent.A -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_A, e.padDown)
                Input.JoypadEvent.B -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_B, e.padDown)
                Input.JoypadEvent.SELECT -> Joypads.setStatus(Joypads.JoypadButtonFlag.SELECT, e.padDown)
                Input.JoypadEvent.START -> Joypads.setStatus(Joypads.JoypadButtonFlag.START, e.padDown)
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