package com.willendless.nes.emulator

import com.willendless.nes.emulator.util.NESException

@ExperimentalUnsignedTypes
class Rom(raw: UByteArray) {
    companion object {
        private val NES_MAGIC_NUMBER = ubyteArrayOf(
                'N'.toInt().toUByte(),
                'E'.toInt().toUByte(),
                'S'.toInt().toUByte(),
                0x1Au).asList()
        private const val PRG_BANK_SIZE = 0x4000
        private const val CHR_BANK_SIZE = 0x2000
    }
    enum class Mirroring {
        VERTICAL,
        HORIZONTAL,
        FOUR_SCREEN
    }

    private val mapper: UByte
    private val screenMirroring: Mirroring
    private val prgRom: List<UByte>
    private val chrRom: List<UByte>

    init {
        if (raw.size < 16) {
            throw  NESException("bad rom format")
        }

        if (raw.slice(0 until 4) != NES_MAGIC_NUMBER) {
            throw NESException("File is not in iNES file format")
        }

        val prgRomBankNum = raw[4]
        val chrRomBankNum = raw[5]
        val control1 = raw[6]
        val control2 = raw[7]

        if ((control2.toInt() shr 2) and 0b11 == 0b10) {
            throw NESException("NES2.0 format is not supported")
        }

        mapper = (((control1 and 0b1111_0000u).toInt() shr 4)
            or (control2 and 0b1111_0000u).toInt()).toUByte()
        screenMirroring = when {
            (control1 and 0b1000u) == 1u.toUByte() -> Mirroring.FOUR_SCREEN
            (control1 and 0b0001u) == 1u.toUByte() -> Mirroring.VERTICAL
            else -> Mirroring.HORIZONTAL
        }

        val prgRomSize = prgRomBankNum.toInt() * PRG_BANK_SIZE
        val chrRomSize = chrRomBankNum.toInt() * CHR_BANK_SIZE
        println("mirroring : ${screenMirroring}," +
                " prgRomBankNum: $prgRomBankNum" +
                " prgRomsize: $prgRomSize" +
                " chrRomBankNum: $chrRomBankNum" +
                " chrRomsize: $chrRomSize")

        val skipTrainer = control1 and 0b100u != 0.toUByte()
        val prgRomBase = 16 + if (skipTrainer) 512 else 0
        val chrRomBase = prgRomBase + prgRomSize
        prgRom = raw.slice(prgRomBase until prgRomBase + prgRomSize)
        chrRom = raw.slice(chrRomBase until chrRomBase + chrRomSize)
    }

    fun getPrgRomLen(): Int = prgRom.size

    fun getPrgRom(): List<UByte> = prgRom

    fun getChrRom(): List<UByte> = chrRom

    fun getScreenMirroring(): Mirroring = screenMirroring
}