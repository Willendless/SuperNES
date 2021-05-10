package com.willendless.nes.game

import com.willendless.nes.emulator.CPU
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Screen
import com.willendless.nes.game.mock.SnakeMainScreen

// This class mainly initializes game assets and CPU.
// Then we will transit to the MainScreen according to game name.
// Only update method is used.
class LoadingScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float) {
        // todo(ljr): initialize game assets and settings
        // todo(ljr): dispatch according to game name
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