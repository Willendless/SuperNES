package com.willendless.nes.game.mock

import android.os.Build
import androidx.annotation.RequiresApi
import com.willendless.nes.BuildConfig
import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.emulator.ppu.PaletteMap
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.framework.Game
import com.willendless.nes.framework.Screen

@RequiresApi(Build.VERSION_CODES.O)
@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
class AlterEgoMainScreen(game: Game): Screen(game) {
    override fun update(deltaTime: Float) {
    }

    override fun present(deltaTime: Float) {
        // render tiles: 0x0 is the addr of low bit of first tile row
        // 0x8 is the addr of high bit of first tile row
        var x = 0
        var y = 0
        for (i in 0 until 256) {
            renderTile(x, y, 0, i)
            x += 8
            if (x == 256) {
                x = 0
                y += 8
            }
        }
    }

    private val TILE_LEN: Int = 16

    // REQUIRES: 1 >= bank >= 0,
    private fun renderTile(x: Int, y: Int, bank: Int, tileIndex: Int) {
        val base = bank + tileIndex * 16
        val tile = PPU.chrRom.slice(base until base + TILE_LEN)
        for (i in 0 until 8) {
            var lower = tile[i].toInt()
            var upper = tile[i + 8].toInt()
            for (j in (0 until 8).reversed()) {
                val index = ((1 and upper) shl 1) or (1 and lower)
                lower = lower shr 1
                upper = upper shr 1
                val color = when (index) {
                    0 -> PaletteMap.getColor(2)
                    1 -> PaletteMap.getColor(10)
                    2 -> PaletteMap.getColor(45)
                    3 -> PaletteMap.getColor(33)
                    else -> unreachable("Unknown palette index")
                }
                game.getGraphics().drawPixel(x + i, y + j, color.toArgb())
            }
        }
    }

    override fun pause() {
    }

    override fun resume() {
    }

    override fun dispose() {
        NESBus.clear()
    }
}