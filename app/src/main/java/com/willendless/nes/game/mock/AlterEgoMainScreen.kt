package com.willendless.nes.game.mock

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.Joypads
import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.emulator.ppu.PaletteMap
import com.willendless.nes.emulator.util.NESException
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Input
import com.willendless.nes.framework.Input.TouchEvent.Companion.TOUCH_DOWN
import com.willendless.nes.framework.Input.TouchEvent.Companion.TOUCH_UP
import com.willendless.nes.framework.Screen
import kotlin.system.exitProcess

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class AlterEgoMainScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float): Boolean {
//        val keyEvents = game.getInput().getKeyEvents()
//        for (e in keyEvents) {
//            when (e.type) {
//                Input.KeyEvent.KEY_DOWN -> {
//                    when (e.KeyChar) {
//                        'w' -> Joypads.setStatus(Joypads.JoypadButtonFlag.UP, true)
//                        's' -> Joypads.setStatus(Joypads.JoypadButtonFlag.DOWN, true)
//                        'a' -> Joypads.setStatus(Joypads.JoypadButtonFlag.LEFT, true)
//                        'd' -> Joypads.setStatus(Joypads.JoypadButtonFlag.RIGHT, true)
//                        'o' -> Joypads.setStatus(Joypads.JoypadButtonFlag.SELECT, true)
//                        'p' -> Joypads.setStatus(Joypads.JoypadButtonFlag.START, true)
//                        'j' -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_A, true)
//                        'k' -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_B, true)
//                        else -> {}
//                    }
//                    Log.d("Events", "handle ${e.KeyChar} key down, mem val ${Joypads.buttonStatus}")
//                }
//                Input.KeyEvent.KEY_UP -> {
//                    when (e.KeyChar) {
//                        'w' -> Joypads.setStatus(Joypads.JoypadButtonFlag.UP, false)
//                        's' -> Joypads.setStatus(Joypads.JoypadButtonFlag.DOWN, false)
//                        'a' -> Joypads.setStatus(Joypads.JoypadButtonFlag.LEFT, false)
//                        'd' -> Joypads.setStatus(Joypads.JoypadButtonFlag.RIGHT, false)
//                        'o' -> Joypads.setStatus(Joypads.JoypadButtonFlag.SELECT, false)
//                        'p'  -> Joypads.setStatus(Joypads.JoypadButtonFlag.START, false)
//                        'j' -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_A, false)
//                        'k' -> Joypads.setStatus(Joypads.JoypadButtonFlag.BUTTON_B, false)
//                        else -> {}
//                    }
//                    Log.d("Events", "handle ${e.KeyChar} key up, mem val ${Joypads.buttonStatus}")
//                }
//                else -> {}
//            }
//        }

        val bottom = 1920
        val right = 1080
        val touchEvents = game.getInput().getTouchEvents()
        Log.d("touchEvents len", "${touchEvents.size}")
        for (e in touchEvents) {
            val value = when (e.type) {
                TOUCH_DOWN -> true
                TOUCH_UP -> false
                else -> true
            }
            Joypads.setStatus(Joypads.JoypadButtonFlag.START, value)
//            when {
//                e.x in (right / 4 * 3..right) && e.y in (bottom / 4 * 3.. bottom / 8 * 7) ->
//                    Joypads.setStatus(Joypads.JoypadButtonFlag.START, value)
//            }
            Log.d("", "${e.type}, $value, (${e.x}, ${e.y}),  mem val ${Joypads.buttonStatus}")
        }

        try {
            return CPU.run(1000)
        } catch (e: NESException) {
            Log.e("CPU", e.msg)
            exitProcess(-1)
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