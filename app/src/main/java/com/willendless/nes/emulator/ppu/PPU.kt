package com.willendless.nes.emulator.ppu

import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.emulator.util.assert

// 7  bit  0
// ---- ----
// BGRs bMmG
// |||| ||||
// |||| |||+- Greyscale (0: normal color, 1: produce a greyscale display)
// |||| ||+-- 1: Show background in leftmost 8 pixels of screen, 0: Hide
// |||| |+--- 1: Show sprites in leftmost 8 pixels of screen, 0: Hide
// |||| +---- 1: Show background
// |||+------ 1: Show sprites
// ||+------- Emphasize red (green on PAL/Dendy)
// |+-------- Emphasize green (red on PAL/Dendy)
// +--------- Emphasize blue

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
        this.chrRom = rom.getChrRom()
        this.mirroring = rom.getScreenMirroing()
    }

    fun tick(cycles: Int) {
        this.cycles += cycles
        if (cycles >= SCAN_LINE_CYCLES_CNT) {
            this.cycles -= SCAN_LINE_CYCLES_CNT
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
        return when (Pair(mirroring, tableIndex)) {
            VERTICAL_TABLE2, VERTICAL_TABLE3 -> (ramOffset - 0x800u).toUShort()
            HORIZONTAL_TABLE1 -> (ramOffset - 0x400u).toUShort()
            HORIZONTAL_TABLE2 -> (ramOffset - 0x400u).toUShort()
            HORIZONTAL_TABLE3 -> (ramOffset - 0x800u).toUShort()
            else -> ramOffset.toUShort()
        }
    }

    // REQUIRES: init() should be called before readUByte()
    fun readUByte(): UByte {
        assert(::chrRom.isInitialized,
            "read ppu addr before chrRom initialized")

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
                dataBuffer = ram[addr.toInt() - 0x2000]
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
        assert(::chrRom.isInitialized,
            "write ppu addr before chrRom initialized")

        val addr = addrReg.get()
        incAddrReg()

        when (addr.toInt()) {
            // write chr rom
            in 0..0x1FFF -> unreachable("chr rom addr: " +
                    "${Integer.toHexString(addr.toInt())} cannot be written")
            in 0x2000..0x2FFF -> {
                if (!statusReg.getStatus(Flag.IGNORE_RAM_WRITE))
                    ram[addr.toInt() - 0x2000] = value
            }
            in 0x3f00..0x3fff -> palettesTable[(addr.toInt() - 0x3f00)]
            else -> unreachable("unexpected ppu memory address" +
                    " ${Integer.toHexString(addr.toInt())}")
        }
    }
}