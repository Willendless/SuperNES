package com.willendless.nes.game.mock

import android.graphics.Color.*
import android.util.Log
import com.willendless.nes.emulator.CPU
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Screen
import java.util.*

@ExperimentalStdlibApi
class SnakeMainScreen(game: Game): Screen(game) {
    @ExperimentalUnsignedTypes
    override fun update(deltaTime: Float) {
        val keyEvents = game.getInput().getKeyEvents()
        var i = 0
        while (i < keyEvents.size) {
            when (keyEvents[i].type) {
                Input.KeyEvent.KEY_DOWN -> {
                    val char = keyEvents[i].KeyChar
                    when (keyEvents[i].KeyChar) {
                        'w' -> CPU.memory.writeUnsignedByte(0xFFu, 0x77u)
                        's' -> CPU.memory.writeUnsignedByte(0xFFu, 0x73u)
                        'a' -> CPU.memory.writeUnsignedByte(0xFFu, 0x61u)
                        'd' -> CPU.memory.writeUnsignedByte(0xFFu, 0x64u)
                        else -> {
                            Log.d("useless key pushed", "${keyEvents[i].KeyChar}")
                        }
                    }
                    Log.d("Events", "handle $char key down, mem val ${CPU.memory[0xFFu]}")
                }
                else -> {}
            }
            i++
        }
        CPU.memory.writeUnsignedByte(0xfeu, (Random().nextInt(16) + 1).toUByte())
        CPU.run(30)
    }

    fun getColor(byte: Int) = when (byte) {
        0 -> BLACK
        1 -> WHITE
        2, 9 -> GRAY
        3, 10 -> RED
        4, 11 -> GREEN
        5, 12 -> BLUE
        6, 13 -> MAGENTA
        7, 14 -> YELLOW
        else -> CYAN
    }

    @ExperimentalUnsignedTypes
    override fun present(deltaTime: Float) {
        // render
        var x = 0
        var y = 0
        for (pos in 0x0200 until 0x0600) {
            val colorByte = CPU.memory[pos.toUShort()]
            val color = getColor(colorByte.toInt())
            game.getGraphics().drawPixel(x, y, color)
            if (x == 31) {
                x = 0
                y++
            }
            else x++
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
    }
}