package com.willendless.nes.emulator.ppu

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.util.nesAssert
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.framework.Graphics

@ExperimentalUnsignedTypes
object PPU {
    // memory address space
    // chrRom: 0x0..0x2000
    lateinit var chrRom: List<UByte>
    // ram: 0x2000..0x2800..mirror to..0x3F00
    private val ram: UByteArray = UByteArray(0x800)
    // palettes: 0x3F00..0x3F20..mirror to..0x4000
    private val palettesTable: UByteArray = UByteArray(32)
    // oam: ppu internal memory
    private val oam: UByteArray = UByteArray(256)
    private var mirroring: Rom.Mirroring = Rom.Mirroring.HORIZONTAL
    // registers
    private var addrReg = AddressReg
    private val controlReg1 = ControllerReg
    private val controlReg2 = MaskReg
    private val statusReg = StatusReg
    private var oamAddrReg: UByte = 0u.toUByte()
    private var scrollReg: UByte = 0u.toUByte()
    // latch
    private var dataBuffer: UByte = 0u.toUByte()
    // scan line bookkeeping
    private var cycles = 0;
    private var scanLine = 0
    private const val SCAN_LINE_CYCLES_CNT = 341
    private const val SCAN_LINE_END = 262
    private const val SCAN_LINE_VBLANK_BASE = 241
    // interrupt
    private var isNMITriggered = false

    fun init(rom: Rom) {
        chrRom = rom.getChrRom()
        mirroring = rom.getScreenMirroring()
    }

    fun reset() {
        ram.fill(0u)
        palettesTable.fill(0u)
        addrReg.clear()
        controlReg1.set(0u)
        controlReg2.set(0u)
        statusReg.clear()
        oamAddrReg = 0u
        scrollReg = 0u
        dataBuffer = 0u
        cycles = 0
        scanLine = 0
    }

    fun tick(deltaCycles: Int) {
        cycles += deltaCycles
        if (cycles >= SCAN_LINE_CYCLES_CNT) {
            cycles -= SCAN_LINE_CYCLES_CNT
            scanLine += 1

            if (scanLine == SCAN_LINE_VBLANK_BASE) {
                statusReg.setStatus(Flag.VBLANK_STATE, true)
                statusReg.setStatus(Flag.IGNORE_RAM_WRITE, false)
                if (controlReg1.getFlag(ControllerRegFlag.NMI_ENABLE))
                    isNMITriggered = true
            }

            if (scanLine >= SCAN_LINE_END) {
                scanLine = 0
                isNMITriggered = false
                statusReg.setStatus(Flag.VBLANK_STATE, false)
            }
        }
    }

    // ENSURES: return true if there is a NMI waiting to be handled, clear it before return
    fun takeNMI(): Boolean {
        val triggered = isNMITriggered
        isNMITriggered = false
        return triggered
    }

    fun dma(buf: UByteArray) {
        buf.copyInto(oam, 0)
    }

    fun writeAddrReg(value: UByte) {
        addrReg.update(value)
    }

    private fun isInVblank() = scanLine >= SCAN_LINE_VBLANK_BASE

    fun writeControlReg1(value: UByte) {
        val prevNMIEn = controlReg1.getFlag(ControllerRegFlag.NMI_ENABLE)
        controlReg1.set(value)
        val curNMIEn = controlReg1.getFlag(ControllerRegFlag.NMI_ENABLE)
        if (!prevNMIEn && curNMIEn && isInVblank()) {
            isNMITriggered = true
        }
    }

    fun writeControlReg2(value: UByte) {
        controlReg2.set(value)
    }

    fun readStatusReg() = statusReg.get()

    fun writeOAMAddrReg(value: UByte) {
        oamAddrReg = value
    }

    fun readOAMUByte(): UByte = oam[oamAddrReg.toInt()]

    // ENSURES: oamAddr will be incremented
    fun writeOAMUByte(value: UByte) {
        oam[oamAddrReg.toInt()] = value
        oamAddrReg++
    }

    fun writeScrollReg(value: UByte) {
        scrollReg = value
    }

    fun incAddrReg() {
        if (controlReg1.getFlag(ControllerRegFlag.VRAM_ADD_AUTOINCREMENT)) {
            addrReg.inc(32u)
        } else {
            addrReg.inc(1u)
        }
    }

    enum class RenderKind {
        BACK_GROUND,
        SPRITE
    }

    // Render screen according to ppu vram.
    @RequiresApi(Build.VERSION_CODES.O)
    fun render(graphics: Graphics) {
        val nameTableBase = vaddrToPaddr(controlReg1.getNameTableBase()).toInt() - 0x2000
//        Log.d("render", "name table base ${Integer.toHexString(nameTableBase.toInt())}")
//        Log.d("render", "tick")
        val backgroundBank = controlReg1.
            getPatternTableBase(ControllerReg.PatternTableKind.BACKGROUND)
        val spriteBank = controlReg1.
            getPatternTableBase(ControllerReg.PatternTableKind.SPRITE)

//        Log.d("sprite table base", "${Integer.toHexString(spriteBank.toInt())}")

        // render background
        for (i in 0 until 0x3C0) {
            val tileIndex = ram[i].toInt()
            val tileY = i / 32
            val tileX = i % 32
            renderBackgroundTile(tileX, tileY, backgroundBank.toInt(), tileIndex, graphics)
        }
        // render sprite
        for (i in oam.indices step 4) {
            val tileX = oam[i + 3].toInt()
            val tileY = oam[i].toInt()
            val tileIndex = oam[i + 1].toInt()
            val attr = oam[i + 2].toInt()
            val flipHorizontal = (attr shr 6 and 1) != 0
            val flipVertical = (attr shr 7 and 1) != 0
            val platteIndex = attr and 0b11
            renderSpriteTile(tileX, tileY, spriteBank.toInt(), tileIndex, platteIndex,
                flipHorizontal, flipVertical, graphics)
        }
    }

    // Byte 0: Y position of top of sprite
    // Byte 1: tile index number within the pattern table
    // Byte 2:
    //    76543210
    //    ||||||||
    //    ||||||++- Palette (4 to 7) of sprite
    //    |||+++--- Unimplemented
    //    ||+------ Priority (0: in front of background; 1: behind background)
    //    |+------- Flip sprite horizontally
    //    +-------- Flip sprite vertically
    // Byte 3: X position of top of sprite
    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderSpriteTile(tileX: Int, tileY: Int, bank: Int,
                                 tileIndex: Int, platteIndex: Int,
                                 isFlipHorizontal: Boolean,
                                 isFlipVertical: Boolean, graphics: Graphics) {
        val tileBase = (bank + tileIndex * 16) % chrRom.size
        val tile = chrRom.slice(tileBase until tileBase + 16)
        val platteTable = getSpritePaletteTable(platteIndex)
        for (r in 0 until 8) {
            var lower = tile[r].toInt()
            var upper = tile[r + 8].toInt()
            for (p in (0 until 8).reversed()) {
                val index = ((1 and upper) shl 1) or (1 and lower)
//                if (index == 0) continue
                lower = lower shr 1
                upper = upper shr 1
                val color = PaletteMap.getColor(platteTable[index].toInt())
                when {
                    !isFlipHorizontal && !isFlipVertical ->
                        graphics.drawPixel(tileX + p, tileY + r, color.toArgb())
                    isFlipHorizontal && !isFlipVertical ->
                        graphics.drawPixel(tileX + 7 - p, tileY + r, color.toArgb())
                    !isFlipHorizontal && isFlipVertical ->
                        graphics.drawPixel(tileX + p, tileY + 7 - r, color.toArgb())
                    isFlipHorizontal && isFlipVertical ->
                        graphics.drawPixel(tileX + 7 - p, tileY + 7 - r, color.toArgb())
                }
            }
        }
    }

    private fun getSpritePaletteTable(platteIndex: Int): UByteArray {
        val base = 0x11 + 4 * platteIndex
        return ubyteArrayOf(palettesTable[0], palettesTable[base],
                palettesTable[base + 1], palettesTable[base + 2])
    }

    // REQUIRES: bank ==
    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderBackgroundTile(tileX: Int, tileY: Int, bank: Int, tileIndex: Int, graphics: Graphics) {
        val base = (bank + tileIndex * 16) % chrRom.size
        val tile = chrRom.slice(base until base + 16)
        val paletteTable = getBackgroundPaletteTable(tileX, tileY)
        for (r in 0 until 8) {
            var lower = tile[r].toInt()
            var upper = tile[r + 8].toInt()
            for (p in (0 until 8).reversed()) {
                val index = ((1 and upper) shl 1) or (1 and lower)
                lower = lower shr 1
                upper = upper shr 1
                val color = PaletteMap.getColor(paletteTable[index].toInt())
                graphics.drawPixel(tileX * 8 + p, tileY * 8 + r, color.toArgb())
            }
        }
    }

    // Get palette from palette table using 4x4 tile coordinates and 64B table following name table
    private fun getBackgroundPaletteTable(tileX: Int , tileY: Int): UByteArray {
        val attributeTableIndex = tileX / 4 + tileY / 4 * 8
        nesAssert(attributeTableIndex < 64,
            "invalid index $attributeTableIndex palette name table is only 64B")

        val attributeByte = ram[0x3C0 + attributeTableIndex]
        // find attribute table index
        val x = tileX % 4 / 2
        val y = tileY % 4 / 2
        val palletIndex = when {
            x == 0 && y == 0 -> attributeByte.toInt() and 0b11
            x == 1 && y == 0 -> attributeByte.toInt() shr 2 and 0b11
            x == 0 && y == 1 -> attributeByte.toInt() shr 4 and 0b11
            x == 1 && y == 1 -> attributeByte.toInt() shr 6 and 0b11
            else -> unreachable("")
        }

        val base = palletIndex * 4 + 1
        return ubyteArrayOf(palettesTable[0], palettesTable[base],
            palettesTable[base + 1], palettesTable[base + 2])
    }

    // Provided to ppu test
    fun setMirroring(mirroring: Rom.Mirroring) {
        this.mirroring = mirroring
    }

    // Transit virtual address of ram (name table) to address
    // This method is for ppu RAM read and write
    private fun vaddrToPaddr(addr: UShort): UShort {
        nesAssert(addr in 0x2000u..0x3EFFu, "invalid vaddr waiting to be transformed")

        val mirroredVaddr = addr and 0b10_1111_1111_1111u
        val ramOffset = mirroredVaddr - 0x2000u
        val tableIndex = ramOffset / 0x400u
        val offset = when {
            ((mirroring == Rom.Mirroring.VERTICAL) && (tableIndex == 2u))
                    || ((mirroring == Rom.Mirroring.VERTICAL) && (tableIndex == 3u))
            -> (ramOffset - 0x800u).toUShort()
            mirroring == Rom.Mirroring.HORIZONTAL && tableIndex == 1u
            -> (ramOffset - 0x400u).toUShort()
            mirroring == Rom.Mirroring.HORIZONTAL && tableIndex == 2u
            -> (ramOffset - 0x400u).toUShort()
            mirroring == Rom.Mirroring.HORIZONTAL && tableIndex == 3u
            -> (ramOffset - 0x800u).toUShort()
            else -> ramOffset.toUShort()
        }
        return (offset + 0x2000u).toUShort()
    }

    fun readUByte(): UByte {
        val addr = addrReg.get()
        incAddrReg()

        return when (addr.toInt()) {
            // read chr rom (pattern tables)
            in 0..0x1FFF -> {
                nesAssert(::chrRom.isInitialized,
                    "read ppu data before chrRom initialized")
                val res = dataBuffer
                dataBuffer = chrRom[addr.toInt()]
                res
            }
            // read ram (name tables)
            in 0x2000..0x3EFF -> {
                val mirroredAddr = addr and 0x2FFFu
                val res = dataBuffer
                dataBuffer = ram[vaddrToPaddr(mirroredAddr).toInt() - 0x2000]
                res
            }
            in 0x3f00..0x3fff -> {
                val mirroredAddr = addr and 0x3F1Fu
                palettesTable[(mirroredAddr.toInt() - 0x3f00) and 0x1F]
            }
            else -> unreachable("unexpected ppu memory address" +
                    " ${Integer.toHexString(addr.toInt())}")
        }
    }

    fun writeUByte(value: UByte) {
        val addr = addrReg.get()
        incAddrReg()

        when (addr.toInt()) {
            // todo: fix bug here
            in 0..0x1FFF -> {}
//                unreachable("chr rom addr: " +
//                    "${Integer.toHexString(addr.toInt())} cannot be written")
            in 0x2000..0x3EFF -> {
                val mirroredAddr = addr and 0x2FFFu
                if (!statusReg.getStatus(Flag.IGNORE_RAM_WRITE))
                    ram[vaddrToPaddr(mirroredAddr).toInt() - 0x2000] = value
            }
            in 0x3f00..0x3fff -> {
                val mirroredAddr = addr and 0x3F1Fu
                palettesTable[(mirroredAddr.toInt() - 0x3f00) and 0x1F] = value
            }
            else -> unreachable("unexpected ppu memory address" +
                    " ${Integer.toHexString(addr.toInt())}")
        }
    }

}