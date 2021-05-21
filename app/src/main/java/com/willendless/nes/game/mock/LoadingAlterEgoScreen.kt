package com.willendless.nes.game.mock

import android.os.Build
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.Bus
import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Screen

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class LoadingAlterEgoScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float) {
        val program = game.getFileIO().readAsset("testGames/AlterEgo.nes").readBytes().toUByteArray()
        val rom = Rom(program)
        NESBus.init(rom)
        PPU.init(rom)
        game.setScreen(AlterEgoMainScreen(game))
    }

    override fun present(deltaTime: Float) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }

}