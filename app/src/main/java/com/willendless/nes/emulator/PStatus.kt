package com.willendless.nes.emulator

@ExperimentalUnsignedTypes
object PStatus {
    var reg: UByte = 0u;
    fun getValUnsigned(): Int = reg.toInt()

    fun n(): Int = (reg.toInt() and 0b1000_0000) shr 7
    fun v(): Int = (reg.toInt() and 0b0100_0000) shr 6
    fun d(): Int = (reg.toInt() and 0b0000_1000) shr 3
    fun i(): Int = (reg.toInt() and 0b0000_0100) shr 2
    fun z(): Int = (reg.toInt() and 0b0000_0010) shr 1
    fun c(): Int = (reg.toInt() and 0b0000_0001)

    fun set(value: UInt) {
        reg = value.toUByte()
    }

    // REQUIRES: value <= 0xFF
    fun set(value: Int) {
        reg = value.toUByte()
    }

    // REQUIRES: this func can only be used by CLC instruction
    fun clearC() {
        reg = reg and 0b1111_1110u
    }

    // REQUIRES: this func can only be used by CLD instruction
    fun clearD() {
        reg = reg and 0b1111_0111u
    }

    // REQUIRES: this func can only be used by CLI instruction
    fun clearI() {
        reg = reg and 0b1111_1011u
    }

    // REQUIRES: this func can only be used by CLV instruction
    fun clearV() {
        reg = reg and 0b1011_1111u
    }

    // Directly set V to 1
    // REQUIRES: this fun can only used by BIT instruction
    fun setV() {
        reg = reg or 0b0100_0000u
    }

    // Directly set C to 1
    // REQUIRES: this fun can only used by shift and SEC instructions
    fun setC() {
        reg = reg or 0b0000_0001u
    }

    // REQUIRES: this func can only used by SED instruction
    fun setD() {
        reg = reg or 0b0000_1000u
    }

    // REQUIRES: this func can only used by SEI and BRK instruction
    fun setI() {
        reg = reg or 0b0000_0100u
    }

    // ENSURES: update N, Z flags based on other
    fun updateNZ(other: Reg8bits) {
        reg = if (other.reg == 0u.toUByte()) reg or 0b0000_0010u
              else reg and 0b1111_1101u
        reg = if (other.reg and 0b1000_0000u > 0u.toUByte()) reg or 0b1000_0000u
              else reg and 0b0111_1111u
    }

    fun updateNZ(other: Int) {
        updateNZ(Reg8bits(other))
    }

    // ENSURES: update Z flag if least significant 8 bits of other is 0
    fun updateZ(other: Int) {
        reg = if (other and 0b1111_1111 == 0) reg or 0b0000_0010u
              else reg and 0b1111_1101u
    }

    // ENSURES: update N flag if most significant bit of other is 1
    fun updateN(other: Int) {
        reg = if (other and 0b1000_0000 > 0) reg or 0b1000_0000u
              else reg and 0b0111_1111u
    }

    // ENSURES: update C flag if other > 255 or other < 0
    fun updateC(other: Int) {
        reg = if (other > 255 || other < 0) reg or 0b0000_0001u
              else reg and 0b1111_1110u
    }

    // ENSURES: update V flag if other < -128 or other > 127
    fun updateV(other: Int) {
        reg = if (other > 127 || other < -128) reg or 0b0100_0000u
              else reg and 0b1011_1111u
    }
}