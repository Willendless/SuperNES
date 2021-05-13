package com.willendless.nes.emulator

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
data class Memory(val size: Int = 0x10000) {
    var mem = UByteArray(size)

//    operator fun get(addr: UShort): UByte = mem[addr.toInt()]

    fun readUShort(addr: UShort): UShort {
        val lo = mem[addr.toInt()]
        val hi = mem[addr.toInt() + 1]
        return hi.toUShort().rotateLeft(8) or lo.toUShort()
    }

    fun readUByte(addr: UShort): UByte = mem[addr.toInt()]

    fun writeUShort(addr: UShort, data: UShort) {
        mem[addr.toInt()] = data.toUByte()
        mem[addr.toInt() + 1] = data.rotateRight(8).toUByte()
    }

    fun writeUByte(addr: UShort, data: UByte) {
        mem[addr.toInt()] = data
    }

    fun populate(source: UByteArray, offset: Int = 0) {
        source.copyInto(mem, offset)
    }

    fun clear() {
        mem.fill(0x0u)
    }
}