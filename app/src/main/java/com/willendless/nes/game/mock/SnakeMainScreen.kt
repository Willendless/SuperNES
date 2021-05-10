package com.willendless.nes.game.mock

import android.view.KeyEvent
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Screen

class SnakeMainScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float) {
        val keyEvents = game.getInput().getKeyEvents()
        var i = 0
        while (i < keyEvents.size) {
            when (keyEvents[i]) {

            }
            i++
        }

    }

    override fun present(deltaTime: Float) {
        TODO("Not yet implemented")
    }

    override fun pause() {
        TODO("Not yet implemented")
    }

    override fun resume() {
        TODO("Not yet implemented")
    }

    override fun dispose() {
        TODO("Not yet implemented")
    }
}