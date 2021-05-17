package com.willendless.nes.emulator.ppu

import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.util.unreachable

@ExperimentalUnsignedTypes
object PPU {
    private lateinit var chrRom: List<UByte>
    var mirroring = Rom.Mirroring.VERTICAL
    val ram: UByteArray = UByteArray(0x2000)
    val palettesTable: UByteArray = UByteArray(32)
    val oam: UByteArray = UByteArray(256)

    private var addrReg = AddressReg  // map to cpu address: 0x2006
    private val controllerReg = Controller()
    private var dataBuffer: UByte = 0u.toUByte()

    fun init(rom: Rom, mirroring: Rom.Mirroring) {
        this.chrRom = rom.prgRom
        this.mirroring = mirroring
    }

    private fun writeAddrReg(value: UByte) {
        addrReg.update(value)
    }

    fun updateController(value: UByte) {
        controllerReg.set(value)
    }

    fun incAddrReg() {
        if (controllerReg.getFlag(Flag.VRAM_ADD_AUTOINCREMENT)) {
            addrReg.inc(32u)
        } else {
            addrReg.inc(1u)
        }
    }

    // REQUIRES: init() should be called before readUByte()
    fun readUByte(): UByte {
        val addr = addrReg.get()
        incAddrReg()

        return when (addr.toInt()) {
            in 0..0x1FFF -> {
                val res = dataBuffer
                dataBuffer = chrRom[addr.toInt()]
                res
            }
            in 0x2000..0x2FFF -> {
                val res = dataBuffer
                dataBuffer = ram[addr.toInt() - 0x2000]
                res
            }
            in 0x3000..0x3eff -> unreachable("ppu mem space 0x3000..0x3eff not used, requested = ${Integer.toHexString(addr.toInt())}")
            in 0x3f00..0x3fff -> palettesTable[(addr.toInt() - 0x3f00)]
            else -> unreachable("unexpected ppu memory address ${Integer.toHexString(addr.toInt())}")
        }
    }

    // REQUIRES: init() should be called before readUByte()
    fun writeUByte() {
        val addr = addrReg.get()
        incAddrReg()

        when (addr.toInt()) {
            // todo
        }
    }
}