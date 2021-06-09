package com.willendless.nes.game

import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Screen

// This class mainly initializes game assets and CPU.
// Then we will transit to the MainScreen according to game name.
// Only update method is used.
class LoadingScreen(val gamePath: String, game: Game): Screen(game) {
    @ExperimentalUnsignedTypes
    @ExperimentalStdlibApi
    override fun update(deltaTime: Float): Boolean {
        val romFile = game.getFileIO().readAsset(gamePath).readBytes().toUByteArray()
        val rom = Rom(romFile)
        NESBus.init(rom)
        CPU.reset()
        game.setScreen(MainScreen(game))
        return false
    }

    override fun present(deltaTime: Float) {
    }

    override fun pause() {
    }

    override fun resume() {
    }

    @ExperimentalStdlibApi
    override fun dispose() {
    }

}