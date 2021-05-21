package com.willendless.nes.emulator.ppu

import android.os.Build
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.emulator.util.assert
import com.willendless.nes.framework.Graphics
import com.willendless.nes.framework.impl.AndroidGraphics

@ExperimentalUnsignedTypes
object PPU {
    // memory address space
    // chrRom: 0x0..0x2000
    lateinit var chrRom: List<UByte>
    // ram: 0x2000..0x2800..mirror to..0x3F00
    private val ram: UByteArray = UByteArray(2048)
    // palettes: 0x3F00..0x3F20..mirror to..0x4000
    private val palettesTable: UByteArray = UByteArray(32)
    // oam: ppu internal memory
    private val oam: UByteArray = UByteArray(256)
    private lateinit var mirroring: Rom.Mirroring
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
        mirroring = rom.getScreenMirroing()
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

    fun writeAddrReg(value: UByte) {
        addrReg.update(value)
    }

    private fun isInVblank() = scanLine >= SCAN_LINE_VBLANK_BASE

    fun writeControlReg1(value: UByte) {
        val prevNMIEn = controlReg1.getFlag(ControllerRegFlag.NMI_ENABLE)
        controlReg1.set(value)
        val curNMIEn = controlReg1.getFlag(ControllerRegFlag.NMI_ENABLE)
        if (!prevNMIEn && curNMIEn && isInVblank())
            statusReg.setStatus(Flag.VBLANK_STATE, true)
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

    // Render screen according to ppu vram.
    @RequiresApi(Build.VERSION_CODES.O)
    fun render(graphics: Graphics) {
        val nameTableBank = controlReg1.getNameTableBase()
        val backgroundBank = controlReg1.
            getBackgroundTableBase(ControllerReg.PatternTableKind.BACKGROUND)

        // each byte map to one tile
        for (i in 0 until 0x3C0) {
            val tileIndex = ram[i].toInt()
//            println("$i tileIndex: $tileIndex")
            val tileY = i / 32 * 8 // pixel based coordinates
            val tileX = i % 32 * 8 // pixel based coordinates

            renderTile(tileX, tileY, backgroundBank.toInt(), tileIndex, graphics)
        }
    }

    // REQUIRES: bank ==
    @RequiresApi(Build.VERSION_CODES.O)
    private fun renderTile(x: Int, y: Int, bank: Int, tileIndex: Int, graphics: Graphics) {
        val base = bank + tileIndex * 16
        val tile = chrRom.slice(base until base + 16)
        for (r in 0 until 8) {
            var upper = tile[r].toInt()
            var lower = tile[r + 8].toInt()
            for (p in (0 until 8).reversed()) {
                val index = ((1 and upper) shl 1) or (1 and lower)
                lower = lower shr 1
                upper = upper shr 1
                val color = when (index) {
                    0 -> PaletteMap.getColor(0x10)
                    1 -> PaletteMap.getColor(0x23)
                    2 -> PaletteMap.getColor(0x27)
                    3 -> PaletteMap.getColor(0x30)
                    else -> unreachable("Unknown palette index")
                }
                graphics.drawPixel(x + p, y + r, color.toArgb())
            }
        }
    }

    private val VERTICAL_TABLE2 = Pair(Rom.Mirroring.VERTICAL, 2);
    private val VERTICAL_TABLE3 = Pair(Rom.Mirroring.VERTICAL, 3);
    private val HORIZONTAL_TABLE1 = Pair(Rom.Mirroring.HORIZONTAL, 1)
    private val HORIZONTAL_TABLE2 = Pair(Rom.Mirroring.HORIZONTAL, 2)
    private val HORIZONTAL_TABLE3 = Pair(Rom.Mirroring.HORIZONTAL, 3)

    // Transit virtual address of ram (name table) to physical address
    fun vaddrToPaddr(addr: UShort): UShort {
        val mirroredVaddr = addr and 0b10_1111_1111_1111u
        val ramOffset = mirroredVaddr - 0x2000u
        val tableIndex = ramOffset / 0x400u
//        println("tableIndex: $tableIndex, mirroring: $mirroring")
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
//        println("vaddr: ${Integer.toHexString(addr.toInt())}, " + "phyaddr: ${Integer.toHexString((offset + 0x2000u).toInt())}")
        return (offset + 0x2000u).toUShort()
    }

    // REQUIRES: init() should be called before readUByte()
    fun readUByte(): UByte {
//        assert(::chrRom.isInitialized,
//            "read ppu addr before chrRom initialized")

        val addr = addrReg.get()
        incAddrReg()

        return when (addr.toInt()) {
            // read chr rom (pattern tables)
            in 0..0x1FFF -> {
                val res = dataBuffer
                dataBuffer = chrRom[addr.toInt()]
                res
            }
            // read ram (name tables)
            in 0x2000..0x2FFF -> {
                val res = dataBuffer
                dataBuffer = ram[vaddrToPaddr(addr).toInt() - 0x2000]
                res
            }
            in 0x3000..0x3eff -> unreachable(
                "ppu mem space 0x3000..0x3eff not used, requested = ${
                    Integer.toHexString(
                        addr.toInt()
                    )
                }"
            )
            in 0x3f00..0x3fff -> palettesTable[(addr.toInt() - 0x3f00)]
            else -> unreachable("unexpected ppu memory address" +
                    " ${Integer.toHexString(addr.toInt())}")
        }
    }

    // REQUIRES: init() should be called before writeByte()
    fun writeUByte(value: UByte) {
//        assert(::chrRom.isInitialized,
//            "write ppu addr before chrRom initialized")

        val addr = addrReg.get()
        incAddrReg()

        when (addr.toInt()) {
            // write chr rom
            in 0..0x1FFF -> unreachable("chr rom addr: " +
                    "${Integer.toHexString(addr.toInt())} cannot be written")
            in 0x2000..0x2FFF -> {
                if (!statusReg.getStatus(Flag.IGNORE_RAM_WRITE))
                    ram[vaddrToPaddr(addr).toInt() - 0x2000] = value
            }
            in 0x3000..0x3eff -> {}
            in 0x3f00..0x3fff -> palettesTable[(addr.toInt() - 0x3f00)]
            else -> unreachable("unexpected ppu memory address" +
                    " ${Integer.toHexString(addr.toInt())}")
        }
    }
}