package com.willendless.nes.game.mock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.willendless.nes.BuildConfig
import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.emulator.ppu.PaletteMap
import com.willendless.nes.emulator.util.NESException
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Screen
import kotlin.system.exitProcess

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class AlterEgoMainScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float) {
        try {
            CPU.run(500)
        } catch (e: NESException) {
            Log.d("CPU", e.msg)
            throw e
        }
    }

    override fun present(deltaTime: Float) {
        PPU.render(game.getGraphics())
    }

    private val TILE_LEN: Int = 16

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        NESBus.clear()
    }
}