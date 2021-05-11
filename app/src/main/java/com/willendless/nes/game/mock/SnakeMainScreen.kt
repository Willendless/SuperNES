package com.willendless.nes.game.mock

import com.willendless.nes.emulator.CPU
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Screen

@ExperimentalStdlibApi
class SnakeMainScreen(game: Game): Screen(game) {
    @ExperimentalUnsignedTypes
    override fun update(deltaTime: Float) {
        val keyEvents = game.getInput().getKeyEvents()
        var i = 0
        while (i < keyEvents.size) {
            when (keyEvents[i].type) {
                Input.KeyEvent.KEY_DOWN -> {
                    when (keyEvents[i].KeyChar) {
                        'w' -> CPU.memory[0xFFu] = 0x77u
                        's' -> CPU.memory[0xFFu] = 0x73u
                        'a' -> CPU.memory[0xFFu] = 0x61u
                        'd' -> CPU.memory[0xFFu] = 0x64u
                        else -> {}
                    }
                }
                else -> {}
            }
            i++
        }
        CPU.run(500)
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