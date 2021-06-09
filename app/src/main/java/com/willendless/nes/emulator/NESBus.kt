package com.willendless.nes.emulator

import com.willendless.nes.emulator.cpu.CPU
import com.willendless.nes.emulator.ppu.PPU
import com.willendless.nes.emulator.util.unreachable

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object NESBus: Bus {
    private const val CPU_RAM_BASE: UShort = 0x0000u
    private const val CPU_RAM_END: UShort = 0x1FFFu

    private const val PRG_ROM_BASE: UShort = 0x8000u
    private const val PRG_ROM_END: UShort = 0xFFFFu

    private const val PPU_REGS_BASE: UShort = 0x2000u
    private const val PPU_REGS_END: UShort = 0x3FFFu
    private const val PPU_CONTROLLER_REG: UShort = 0x0u // write only
    private const val PPU_MASK_REG: UShort = 0x1u // write only
    private const val PPU_STATUS_REG: UShort = 0x2u // read only
    private const val PPU_OAM_ADDRESS_REG: UShort = 0x3u // write only
    private const val PPU_OAM_DATA_REG: UShort = 0x4u // read/write
    private const val PPU_SCROLL_REG: UShort = 0x5u // write only
    private const val PPU_ADDRESS_REG: UShort = 0x6u // write only
    private const val PPU_DATA_REG: UShort = 0x7u // read/write
    private const val PPU_OAM_DMA: UShort = 0x4014u // write only

    private const val APU_REGS_BASE: UShort = 0x4000u
    private const val APU_REGS_END: UShort = 0x4013u

    private val cpuRAM = UByteArray(0x800)
    private var rom: Rom? = null
    private val ppu =  PPU
    private var cycles = 0
    private var joypad = Joypads

    fun init(rom: Rom) {
        this.rom = rom
        ppu.init(rom)
    }

    override fun readUByte(addr: UShort): UByte = when (addr) {
        in CPU_RAM_BASE..CPU_RAM_END -> {
            val mirroredAddress = addr and 0b00000111_11111111u
            cpuRAM[mirroredAddress.toInt()]
        }
        in PPU_REGS_BASE..PPU_REGS_END -> {
            when (addr and 0b111u) {
                PPU_CONTROLLER_REG, PPU_MASK_REG, PPU_OAM_ADDRESS_REG,
                PPU_SCROLL_REG, PPU_ADDRESS_REG -> unreachable("Unable to read from write only registers")
                PPU_OAM_DATA_REG -> ppu.readOAMUByte()
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
        in APU_REGS_BASE..APU_REGS_END -> 0u
        in PPU_OAM_DMA..PPU_OAM_DMA -> unreachable("Unable to read from write only OAM DMA Reg")
        // SND_CHN and JOYstick
        in 0x4015u..0x4015u -> 0u
        in 0x4016u..0x4016u -> joypad.read()
        in 0x4017u..0x4017u -> 0u
        else -> unreachable("bus read can not handled addr 0x${Integer.toHexString(addr.toInt())}")
    }

    private val buf = UByteArray(0xFF)
    override fun writeUByte(addr: UShort, data: UByte) {
        when (addr) {
            in CPU_RAM_BASE..CPU_RAM_END -> {
                val mirroredAddress = addr and 0b00000111_11111111u
                cpuRAM[mirroredAddress.toInt()] = data
            }
            in PPU_REGS_BASE..PPU_REGS_END -> {
                when (addr and 0b111u) {
                    PPU_CONTROLLER_REG -> ppu.writeControllerReg(data)
                    PPU_MASK_REG -> ppu.writeMaskReg(data)
                    PPU_STATUS_REG -> unreachable("Unable to write to " +
                            "read only register ppu:status")
                    PPU_OAM_ADDRESS_REG -> ppu.writeOAMAddrReg(data)
                    PPU_OAM_DATA_REG -> ppu.writeOAMUByte(data)
                    PPU_SCROLL_REG -> ppu.writeScrollReg(data)
                    PPU_ADDRESS_REG -> ppu.writeAddrReg(data)
                    PPU_DATA_REG -> ppu.writeUByte(data)
                    PPU_OAM_DMA-> unreachable("Unable to read from write only register $addr")
                    else -> unreachable("addr: ${Integer.toHexString(addr.toInt())}" +
                            " not in PPU range")
                }
            }
            in PRG_ROM_BASE..PRG_ROM_END -> unreachable("unable to write to rom space")
            // DMA
            in PPU_OAM_DMA..PPU_OAM_DMA -> {
                val base = data.toInt() shl 8
                for (i in 0 until 0xFF) {
                    buf[i] = readUByte((base + i).toUShort())
                }
                ppu.dma(buf)
            }
            in APU_REGS_BASE..APU_REGS_END -> {}
            // SND_CHN and JOYstick
            in 0x4015u..0x4015u -> {}
            in 0x4016u..0x4016u -> joypad.write(data)
            in 0x4017u..0x4017u -> {}
            else -> unreachable("bus can not handled addr 0x${Integer.toHexString(addr.toInt())}")
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
        cycles = 0
        cpuRAM.fill(0u)
        CPU.reset()
        PPU.reset()
    }

    override fun tick(cycles: Int) {
        this.cycles += cycles
        ppu.tick(cycles * 3)
    }

    override fun pollNMIStatus(): Boolean = ppu.takeNMI()
}