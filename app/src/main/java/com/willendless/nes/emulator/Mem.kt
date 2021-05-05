package com.willendless.nes.emulator

data class Mem(val size: Int = 0x10000) {
    var mem = UByteArray(size)

    private operator fun get(addr: Int): UByte = mem[addr]
    private operator fun get(addr: UInt): UByte = mem[addr.toInt()]
    operator fun set(addr: Int, data: UByte) {
        mem[addr] = data
    }

    fun readUnsignedShort(addr: Int): Int {
        val lo = this[addr]
        val hi = this[addr+1]
        return (hi.toInt() shl 8) +  lo.toInt()
    }

    fun readUnsignedByte(addr: Int): Int = this[addr].toInt()

    fun readSignedByte(addr: Int): Int = this[addr].toByte().toInt()

    fun writeUnsignedByte(addr: Int, data: Int) {
        this[addr] = data.toUByte()
    }

    fun writeUnsignedShort(addr: Int, data: Int) {
        val hi = (data shr 8).toUByte()
        val lo = data.toUByte()
        this[addr] = lo
        this[addr + 1] = hi
    }
}