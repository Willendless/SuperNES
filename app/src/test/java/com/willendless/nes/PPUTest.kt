package com.willendless.nes

import com.willendless.nes.emulator.Rom
import com.willendless.nes.emulator.ppu.PPU
import org.junit.Test
import org.junit.Assert.*

@ExperimentalUnsignedTypes
class PPUTest {
    private val ppu = PPU

    @Test
    fun test_ppu_ram_write() {
        ppu.writeAddrReg(0x23u)
        ppu.writeAddrReg(0x05u)
        ppu.writeUByte(0x66u)
        ppu.writeAddrReg(0x23u)
        ppu.writeAddrReg(0x05u)
        ppu.readUByte()
        assertEquals(0x66.toUByte(), ppu.readUByte())
    }

    @Test
    fun test_ppu_ram_read() {
        ppu.writeControlReg1(0u)
        ppu.writeAddrReg(0x23u)
        ppu.writeAddrReg(0x05u)
        ppu.writeUByte(0x01u)
        ppu.writeAddrReg(0x23u)
        ppu.writeAddrReg(0x04u)
        ppu.readUByte()
        ppu.readUByte()
        assertEquals(0x01.toUByte(), ppu.readUByte())
    }

    @Test
    fun test_ppu_ram_read_step_32() {
        ppu.writeControlReg1(0b100u)
        ppu.writeAddrReg(0x21u)
        ppu.writeAddrReg(0xffu)
        ppu.writeUByte(0x77u)
        ppu.writeAddrReg(0x21u)
        ppu.writeAddrReg(0xdfu)
        ppu.readUByte()
        assertEquals(0x0u.toUByte(), ppu.readUByte())
        assertEquals(0x77u.toUByte(), ppu.readUByte())
    }

    // Horizontal
    // [0x2000 A] [0x2400 a]
    // [0x2800 B] [0x2C00 b]
    @Test
    fun test_ppu_horizontal_mirroring() {
        ppu.setMirroring(Rom.Mirroring.HORIZONTAL)
        ppu.writeAddrReg(0x24u)
        ppu.writeAddrReg(0x05u)
        ppu.writeUByte(0x66u)
        ppu.writeAddrReg(0x28u)
        ppu.writeAddrReg(0x05u)
        ppu.writeUByte(0x77u)

        ppu.writeAddrReg(0x20u)
        ppu.writeAddrReg(0x05u)
        ppu.readUByte()
        assertEquals(0x66u.toUByte(), ppu.readUByte())

        ppu.writeAddrReg(0x2Cu)
        ppu.writeAddrReg(0x05u)
        ppu.readUByte()
        assertEquals(0x77u.toUByte(), ppu.readUByte())
    }

    // Vertical
    // [0x2000 A] [0x2400 B]
    // [0x2800 a] [0x2C00 b]
    @Test
    fun test_ppu_vertical_mirroring() {
        ppu.setMirroring(Rom.Mirroring.VERTICAL)
        ppu.writeAddrReg(0x20u)
        ppu.writeAddrReg(0x05u)
        ppu.writeUByte(0x66u)
        ppu.writeAddrReg(0x28u)
        ppu.writeAddrReg(0x05u)
        ppu.readUByte()
        assertEquals(0x66u.toUByte(), ppu.readUByte())
        ppu.writeAddrReg(0x2Cu)
        ppu.writeAddrReg(0x77u)
        ppu.writeUByte(0x55u)
        ppu.writeAddrReg(0x24u)
        ppu.writeAddrReg(0x77u)
        ppu.readUByte()
        assertEquals(0x55u.toUByte(), ppu.readUByte())
    }
}