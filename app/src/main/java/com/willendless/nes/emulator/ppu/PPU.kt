package com.willendless.nes.emulator.ppu

import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.util.unreachable
import com.willendless.nes.emulator.util.assert

@ExperimentalUnsignedTypes
object PPU {
    // memory address space
    private lateinit var chrRom: List<UByte>
    private lateinit var mirroring: Rom.Mirroring
    private val ram: UByteArray = UByteArray(2048)
    private val palettesTable: UByteArray = UByteArray(32)
    private val oam: UByteArray = UByteArray(256)
    // registers
    private var addrReg = AddressReg
    private val controlReg1 = ControlReg1
    private val controlReg2 = ControlReg2
    private val statusReg = StatusReg
    private var oamAddrReg: UByte = 0u.toUByte()
    private var scrollReg: UByte = 0u.toUByte()
    // latch
    private var dataBuffer: UByte = 0u.toUByte()
    // scan line bookkeeping
    private var cycles = 0;
    private var scanLine = 0
    private const val SCAN_LINE_CYCLES_CNT = 341
    private const val SCAN_LINE_CNT = 262
    private const val SCAN_LINE_INT = 241

    fun init(rom: Rom) {
        this.chrRom = rom.getPrgRom()
        this.mirroring = rom.getScreenMirroing()
    }

    fun tick(cycles: Int) {
        this.cycles += cycles
        if (cycles >= SCAN_LINE_CYCLES_CNT) {
            this.cycles -= SCAN_LINE_CYCLES_CNT
            scanLine += 1

            if (scanLine == SCAN_LINE_INT
                && controlReg1.getFlag(ControlReg1Flag.NMI_ENABLE)) {
                statusReg.setStatus(Flag.VBLANK_INTERRUPT_OCCUR, true)
                unreachable("todo")
            }

            if (scanLine >= SCAN_LINE_CNT) {
                scanLine = 0
                statusReg.setStatus(Flag.VBLANK_INTERRUPT_OCCUR, false)
            }
        }
    }

    fun writeAddrReg(value: UByte) {
        addrReg.update(value)
    }

    fun writeControlReg1(value: UByte) {
        controlReg1.set(value)
    }

    fun writeControlReg2(value: UByte) {
        controlReg2.set(value)
    }

    fun readStatusReg() = statusReg.get()

    fun writeOAMAddrReg(value: UByte) {
        oamAddrReg = value
    }

    fun writeOAMUByte(value: UByte) {
        oam[oamAddrReg.toInt()] = value
    }

    fun writeScrollReg(value: UByte) {
        scrollReg = value
    }

    fun incAddrReg() {
        if (controlReg1.getFlag(ControlReg1Flag.VRAM_ADD_AUTOINCREMENT)) {
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