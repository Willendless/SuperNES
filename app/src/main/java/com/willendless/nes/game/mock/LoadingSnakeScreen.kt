package com.willendless.nes.game.mock

import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Screen

// This class mainly initializes game assets and CPU.
// Then we will transit to the MainScreen according to game name.
// Only update method is used.
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class LoadingSnakeScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float) {
        val program = game.getFileIO().readAsset("testGames/snake.nes").readBytes().toUByteArray()
        CPU.load(program, 0x8000)
        CPU.reset()
        game.setScreen(SnakeMainScreen(game))
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