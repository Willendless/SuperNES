package com.willendless.nes

import com.willendless.nes.emulator.Bus

@ExperimentalUnsignedTypes
@ExperimentalStdlibApi
object RawBus : Bus {
    var mem = UByteArray(0x10000)   // 64KiB

    override fun readUByte(addr: UShort): UByte = mem[addr.toInt()]

    override fun writeUByte(addr: UShort, data: UByte) {
        mem[addr.toInt()] = data
    }

    override fun populate(source: UByteArray, offset: Int) {
        source.copyInto(mem, offset)
    }

    override fun clear() {
        mem.fill(0x0u)
    }
}