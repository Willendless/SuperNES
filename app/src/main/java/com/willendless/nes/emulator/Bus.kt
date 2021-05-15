package com.willendless.nes.emulator

import com.willendless.nes.emulator.util.unreachable

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object Bus: Mem {
    private const val CPU_RAM_BASE: UShort = 0x0000u
    private const val CPU_RAM_END: UShort = 0x1FFFu
    private const val PPU_REGS_BASE: UShort = 0x2000u
    private const val PPU_REGS_END: UShort = 0x3FFFu
    private const val PRG_ROM_BASE: UShort = 0x8000u
    private const val PRG_ROM_END: UShort = 0xFFFFu

    private val cpuRAM = UByteArray(0x800)
    private var rom: Rom? = null

    override fun readUByte(addr: UShort): UByte = when (addr) {
        in CPU_RAM_BASE..CPU_RAM_END -> {
            val mirroredAddress = addr and 0b00000111_11111111u
            cpuRAM[mirroredAddress.toInt()]
        }
        in PPU_REGS_BASE..PPU_REGS_END -> {
            val mirroredAddress = addr and 0b00000000_00000111u
            cpuRAM[mirroredAddress.toInt()]
        }
        in PRG_ROM_BASE..PRG_ROM_END -> {
            rom?.apply {
                var address = addr - 0x8000u
                if (getPrgRomLen() == 0x4000 && address >= 0x4000u)
                    address %= 0x4000u
                return prgRom[address.toInt()]
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
                val mirroredAddress = addr and 0b00000000_00000111u
                cpuRAM[mirroredAddress.toInt()] = data
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
}