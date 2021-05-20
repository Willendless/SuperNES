package com.willendless.nes.emulator

import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.emulator.util.unreachable
import java.net.URLStreamHandler

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object Bus: Mem {
    private const val CPU_RAM_BASE: UShort = 0x0000u
    private const val CPU_RAM_END: UShort = 0x1FFFu
    private const val PPU_REGS_BASE: UShort = 0x2000u
    private const val PPU_REGS_END: UShort = 0x3FFFu
    private const val PRG_ROM_BASE: UShort = 0x8000u
    private const val PRG_ROM_END: UShort = 0xFFFFu
    private const val PPU_CONTROL_REG_1: UShort = 0x2000u // write only
    private const val PPU_CONTROL_REG_2: UShort = 0x2001u // write only
    private const val PPU_STATUS_REG: UShort = 0x2002u // read only
    private const val PPU_OAM_ADDRESS_REG: UShort = 0x2003u // write only
    private const val PPU_OAM_DATA_REG: UShort = 0x2004u // write only
    private const val PPU_SCROLL: UShort = 0x2005u // write only
    private const val PPU_ADDRESS_REG: UShort = 0x2006u // write only
    private const val PPU_DATA_REG: UShort = 0x2007u // read/write
    private const val PPU_OAM_DMA: UShort = 0x4014u // write only

    private val cpuRAM = UByteArray(0x800)
    private var rom: Rom? = null
    private val ppu =  PPU
    private var cycles = 0

    override fun readUByte(addr: UShort): UByte = when (addr) {
        in CPU_RAM_BASE..CPU_RAM_END -> {
            val mirroredAddress = addr and 0b00000111_11111111u
            cpuRAM[mirroredAddress.toInt()]
        }
        in PPU_REGS_BASE..PPU_REGS_END -> {
            when (addr and 0b111u) {
                PPU_CONTROL_REG_1, PPU_CONTROL_REG_2, PPU_OAM_ADDRESS_REG,
                PPU_OAM_DATA_REG, PPU_SCROLL, PPU_ADDRESS_REG,
                PPU_OAM_DMA-> unreachable("Unable to read from write only register $addr")
                PPU_STATUS_REG -> ppu.readStatusReg()
                PPU_DATA_REG -> ppu.readUByte()
                else -> unreachable("addr: ${Integer.toHexString(addr.toInt())}" +
                        " not in PPU range")
            }
        }
        in PRG_ROM_BASE..PRG_ROM_END -> {
            rom?.apply {
                var address = addr - 0x8000u
                if (getPrgRomLen() == 0x4000 && address >= 0x4000u)
                    address %= 0x4000u
                return getPrgRom()[address.toInt()]
            }
            unreachable("no rom in the system")
        }
        else -> unreachable("bus can not handled addr $addr")
    }

    override fun writeUByte(addr: UShort, data: UByte) {
        when (addr) {
            in CPU_RAM_BASE..CPU_RAM_END -> {
                val mirroredAddress = addr and 0b00000111_11111111u
                cpuRAM[mirroredAddress.toInt()] = data
            }
            in PPU_REGS_BASE..PPU_REGS_END -> {
                when (addr and 0b111u) {
                    PPU_CONTROL_REG_1 -> ppu.writeControlReg1(data)
                    PPU_CONTROL_REG_2 -> ppu.writeControlReg2(data)
                    PPU_STATUS_REG -> unreachable("Unable to write to " +
                            "read only register ppu:status")
                    PPU_OAM_ADDRESS_REG -> ppu.writeOAMAddrReg(data)
                    PPU_OAM_DATA_REG -> ppu.writeOAMUByte(data)
                    PPU_SCROLL -> ppu.writeScrollReg(data)
                    PPU_ADDRESS_REG -> ppu.writeAddrReg(data)
                    PPU_DATA_REG -> ppu.writeUByte(data)
                    PPU_OAM_DMA-> unreachable("Unable to read from write only register $addr")
                    else -> unreachable("addr: ${Integer.toHexString(addr.toInt())}" +
                            " not in PPU range")
                }
            }
            in PRG_ROM_BASE..PRG_ROM_END -> unreachable("unable to write to rom space")
            else -> unreachable("bus can not handled addr $addr")
        }
    }

    override fun populate(source: UByteArray, offset: Int) {
        when (offset.toUShort()) {
            in CPU_RAM_BASE..CPU_RAM_END -> source.copyInto(cpuRAM, offset)
            in PRG_ROM_BASE..PRG_ROM_END -> rom = Rom(source)
            else -> unreachable("unable to populate to $offset")
        }
    }

    override fun clear() {
        cpuRAM.fill(0u)
    }

    override fun tick(cycles: Int) {
        this.cycles += cycles
        ppu.tick(cycles * 3)
    }
}