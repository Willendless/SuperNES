package com.willendless.nes.emulator

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
interface Mem {
    fun readUByte(addr: UShort): UByte

    fun readUShort(addr: UShort): UShort {
        val lo = readUByte(addr)
        val hi = readUByte((addr + 1u).toUShort())
        return hi.toUShort().rotateLeft(8) or lo.toUShort()
    }

    fun writeUByte(addr: UShort, data: UByte)

    fun writeUShort(addr: UShort, data: UShort) {
        writeUByte(addr, data.toUByte())
        writeUByte((addr + 1u).toUShort(), data.rotateRight(8).toUByte())
    }

    fun populate(source: UByteArray, offset: Int)

    fun clear()
}