package com.willendless.nes.game.mock

import com.willendless.nes.emulator.CPU
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Screen

class SnakeMainScreen(game: Game): Screen(game) {
    @ExperimentalUnsignedTypes
    override fun update(deltaTime: Float) {
        val keyEvents = game.getInput().getKeyEvents()
        var i = 0
        while (i < keyEvents.size) {
            when (keyEvents[i].type) {
                Input.KeyEvent.KEY_DOWN -> {
                    when (keyEvents[i].KeyChar) {
                        'w' -> CPU.memory.writeUnsignedByte(0xff, 0x77)
                        's' -> CPU.memory.writeUnsignedByte(0xff, 0x73)
                        'a' -> CPU.memory.writeUnsignedByte(0xff, 0x61)
                        'd' -> CPU.memory.writeUnsignedByte(0xff, 0x64)
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