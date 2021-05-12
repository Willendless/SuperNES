package com.willendless.nes.emulator

import android.os.Build
import androidx.annotation.RequiresApi

@ExperimentalStdlibApi
@ExperimentalUnsignedTypes
object CPU {
    // TODO: make these private and expose getters
    var a: UByte = 0u   // Only instructions have type; registers don't
    var x: UByte = 0u
    var y: UByte = 0u
    var pc: UShort = 0u
    var sp: UByte = 0u
    val status = PStatus
    val memory = Memory(0x10000)
    private val opcodeMap = OpcodeMap

    @RequiresApi(Build.VERSION_CODES.N)
    fun loadAndRun(program: UByteArray) {
        load(program, 0x8000)
        reset()
        run()
    }

    fun load(program: UByteArray, offset: Int) {
        // insert cartridge
        memory.populate(program, offset)
        // reset vector
        memory[0xFFFCu] = offset.toUShort()
    }

    fun reset() {
        a = 0u
        x = 0u
        y = 0u
        sp = 0xfdu // See https://github.com/bugzmanov/nes_ebook/issues/10
        pc = memory.readUShort(0xFFFCu)
        status(0u)
    }

    // Get current instruction byte without range check
    // and increment pc.
    // SIDE EFFECT: the value of pc will be incremented by 1.
    private fun fetchCode(): UByte = memory[pc++]

    // Get current instruction byte without range check.
    // No side effect.
    private fun peekCode(): UByte = memory[pc]

    // Return operand according to addressing mode.
    // No side effect to pc or any other registers.
    // REQUIRES: before called, pc should point to the second byte of the instruction.
    // ENSURES: return the memory address of the operand
    private fun getOperandAddress(mode: AddressMode): UShort {
        return when (mode) {
            AddressMode.Immediate -> pc
            AddressMode.ZeroPage -> peekCode().toUShort()
            AddressMode.ZeroPageX -> (peekCode() + x).toUByte().toUShort()  // zero page wrap around
            AddressMode.ZeroPageY -> (peekCode() + y).toUByte().toUShort()
            AddressMode.Absolute -> pc
            AddressMode.AbsoluteX -> (memory.readUShort(pc) + x).toUShort()
            AddressMode.AbsoluteY -> (memory.readUShort(pc) + y).toUShort()
            AddressMode.Indirect -> memory.readUShort(memory.readUShort(pc))
            AddressMode.IndirectX -> memory.readUShort((memory[pc] + x).toUShort())
            AddressMode.IndirectY -> (memory.readUShort(memory[pc].toUShort()) + y).toUShort()
            AddressMode.NoneAddressing -> unreachable("$mode not supported")
        }
    }

    private fun lda(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = memory[addr]
        status.updateZN(a)
    }

    private fun ldx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        x = memory[addr]
        status.updateZN(x)
    }

    private fun ldy(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        y = memory[addr]
        status.updateZN(y)
    }

    private fun sta(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        memory[addr] = a
    }

    private fun stx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        memory[addr] = x
    }

    private fun sty(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        memory[addr] = y
    }

    private fun tax() {
        x = a
        status.updateZN(x)
    }

    private fun tay() {
        y = a
        status.updateZN(y)
    }

    private fun tsx() {
        x = sp
        status.updateZN(x)
    }

    private fun txa() {
        a = sp
        status.updateZN(a)
    }

    private fun txs() {
        sp = x
        status.updateZN(sp)
    }

    private fun tya() {
        a = y
        status.updateZN(a)
    }

    private fun pha() {
        val addr = (0x100u + sp--).toUShort()
        memory[addr] = a
    }

    private fun php() {
        // https://wiki.nesdev.com/w/index.php/CPU_status_flag_behavior
        val addr = (0x100u + sp--).toUShort()
        memory[addr] = status.toUByte() or Flag.BREAK.mask or Flag.BREAK2.mask
    }

    private fun pla() {
        val addr = (0x100u + ++sp).toUShort()
        a = memory[addr]
        status.updateZN(a)
    }

    private fun plp() {
        val addr = (0x100u + ++sp).toUShort()
        status(memory[addr])
        // TODO: why?
        status[Flag.BREAK] = false
        status[Flag.BREAK2] = true
    }

    private fun and(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = a and memory[addr]
        status.updateZN(a)
    }

    private fun ora(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = a or memory[addr]
        status.updateZN(a)
    }

    private fun eor(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = a xor memory[addr]
        status.updateZN(a)
    }

    private fun bit(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = memory[addr]
        status[Flag.OVERFLOW] = (value and Flag.OVERFLOW.mask) != 0u.toUByte()
        status.updateZN(value)
    }

    private fun adc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val sum = a + memory[addr] + if (status[Flag.CARRY]) 1u else 0u
        status[Flag.CARRY] = sum > UByte.MAX_VALUE
        status[Flag.OVERFLOW] =
            (memory[addr].toUInt() xor sum) and (a.toUInt() xor sum) and 0b1000_0000u != 0u
        a = sum.toUByte()
        status.updateZN(a)
    }

    private fun sbc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val diff = a - memory[addr] - if (status[Flag.CARRY]) 0u else 1u
        status[Flag.CARRY] = diff > UByte.MAX_VALUE
        status[Flag.OVERFLOW] =
            (memory[addr].toUInt() xor diff) and (a.toUInt() xor diff) and 0b1000_0000u != 0u
        a = diff.toUByte()
        status.updateZN(a)
    }

    private fun cmp(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        status[Flag.CARRY] = a >= memory[addr]
        status.updateZN((a - memory[addr]).toUByte())
    }

    private fun cpx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        status[Flag.CARRY] = x >= memory[addr]
        status.updateZN((x - memory[addr]).toUByte())
    }

    private fun cpy(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        status[Flag.CARRY] = y >= memory[addr]
        status.updateZN((y - memory[addr]).toUByte())
    }

    private fun inc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val res = (memory[addr] + 1u).toUByte()
        memory[addr] = res
        status.updateZN(res)
    }

    private fun inx() {
        x++
        status.updateZN(x)
    }

    private fun iny() {
        y++
        status.updateZN(y)
    }

    private fun dec(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val res = (memory[addr] - 1u).toUByte()
        memory[addr] = res
        status.updateZN(res)
    }

    private fun dex() {
        x--
        status.updateZN(x)
    }

    private fun dey() {
        y--
        status.updateZN(y)
    }

    private fun asl(addressMode: AddressMode) {
        val value = when (addressMode) {
            AddressMode.NoneAddressing -> {
                status[Flag.CARRY] = (a and 0b1000_0000u) != 0.toUByte()
                a = (a.toInt() shl 1).toUByte()
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                status[Flag.CARRY] = (memory[addr] and 0b1000_0000u) != 0.toUByte()
                memory[addr] = (memory[addr].toInt() shl 1).toUByte()
                memory[addr]
            }
        }
        status.updateZN(value)
    }

    private fun lsr(addressMode: AddressMode) {
        val value = when (addressMode) {
            AddressMode.NoneAddressing -> {
                status[Flag.CARRY] = (a and 1u) != 0.toUByte()
                a = (a.toInt() shr 1).toUByte()
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                status[Flag.CARRY] = (memory[addr] and 1u) != 0.toUByte()
                memory[addr] = (memory[addr].toInt() shr 1).toUByte()
                memory[addr]
            }
        }
        status.updateZN(value)
    }

    private fun rol(addressMode: AddressMode) {
        val carry = status[Flag.CARRY]
        val value = when (addressMode) {
            AddressMode.NoneAddressing -> {
                status[Flag.CARRY] = (a and 0b1000_0000u) != 0.toUByte()
                a = (a.toInt() shl 1).toUByte()
                if (carry) a = a or 0b0000_0001u
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                status[Flag.CARRY] = (memory[addr] and 0b1000_0000u) != 0.toUByte()
                memory[addr] = (memory[addr].toInt() shl 1).toUByte()
                if (carry) memory[addr] = memory[addr] or 0b0000_0001u
                memory[addr]
            }
        }
        status.updateZN(value)
    }

    private fun ror(addressMode: AddressMode) {
        val carry = status[Flag.CARRY]
        val value = when (addressMode) {
            AddressMode.NoneAddressing -> {
                status[Flag.CARRY] = (a and 1u) != 0.toUByte()
                a = (a.toInt() shr 1).toUByte()
                if (carry) a = a or 0b1000_0000u
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                status[Flag.CARRY] = (memory[addr] and 1u) != 0.toUByte()
                memory[addr] = (memory[addr].toInt() shr 1).toUByte()
                if (carry) memory[addr] = memory[addr] or 0b1000_0000u
                memory[addr]
            }
        }
        status.updateZN(value)
    }

    private fun jmp(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        pc = memory.readUShort(addr)
    }

    private fun jsr(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        println("addr: ${java.lang.Integer.toHexString(addr.toInt())}")
        val jumpAddr = memory.readUShort(addr)
        val retAddr = pc + 2u

        memory[(0x100u + sp - 1u).toUShort()] = (retAddr - 1u).toUShort()
        sp--
        sp--
        pc = jumpAddr
    }

    private fun rts() {
        val retAddr = memory[(0x100u + sp + 1u).toUShort()] + 1u
        sp++
        sp++
        pc = retAddr.toUShort()
    }

    private fun branch() {
        pc = (pc + peekCode()).toUShort()
    }

    // TODO
    private fun brk() {
//        memory.writeUnsignedShort(sp.getValUnsigned(), pc.getValUnsigned())
//        sp++
//        memory.writeUnsignedByte(sp.getValUnsigned() or 0b0011_0000, status.toUByte())
//        sp++
//        pc.set(memory.readUnsignedShort(0xFFFE))
//        // side effect of brk is set I to 1
//        status.setI()
    }

    private fun rti() {
//        sp--
//        status.set(memory.readUnsignedByte(sp.getValUnsigned()))
//        sp--
//        pc.set(memory.readUnsignedShort(sp.getValUnsigned()))
    }

    // Run game in the memory begin from 0x8000.
    fun run(timeout_ms: Long = 10_000) {
        val startTime = System.nanoTime() / 1000_000
        while (true) {
            val curTime = System.nanoTime() / 1000_000
            if (curTime - startTime > timeout_ms) break

            val code = fetchCode()
            val opcode = opcodeMap.getOpcode(code)
            val curPc = pc

            println("[${java.lang.Integer.toHexString(pc.toInt() - 1)}]: $opcode")

            // TODO: reflection with `getDeclaredField`?
            when (opcode.code.toUInt()) {
                // load/store
                0xA9u, 0xA5u, 0xB5u, 0xADu, 0xBDu, 0xB9u, 0xA1u, 0xB1u -> lda(opcode.mode)
                0xA2u, 0xA6u, 0xB6u, 0xAEu, 0xBEu -> ldx(opcode.mode)
                0xA0u, 0xA4u, 0xB4u, 0xACu, 0xBCu -> ldy(opcode.mode)
                0x85u, 0x95u, 0x8Du, 0x9Du, 0x99u, 0x81u, 0x91u -> sta(opcode.mode)
                0x86u, 0x96u, 0x8Eu -> stx(opcode.mode)
                0x84u, 0x94u, 0x8Cu -> sty(opcode.mode)
                // transfer
                0xAAu -> tax() // x = a
                0xA8u -> tay()
                0xBAu -> tsx()
                0x8Au -> txa()
                0x9Au -> txs()
                0x98u -> tya()
                // stack
                0x48u -> pha()
                0x08u -> php()
                0x68u -> pla()
                0x28u -> plp()
                // logic
                0x29u, 0x25u, 0x35u, 0x2Du, 0x3Du, 0x39u, 0x21u, 0x31u -> and(opcode.mode)
                0x09u, 0x05u, 0x15u, 0x0Du, 0x1Du, 0x19u, 0x01u, 0x11u -> ora(opcode.mode)
                0x49u, 0x45u, 0x55u, 0x4Du, 0x5Du, 0x59u, 0x41u, 0x51u -> eor(opcode.mode)
                0x24u, 0x2Cu -> bit(opcode.mode)
                // arithmetic
                0x69u, 0x65u, 0x75u, 0x6Du, 0x7Du, 0x79u, 0x61u, 0x71u -> adc(opcode.mode)
                0xE9u, 0xE5u, 0xF5u, 0xEDu, 0xFDu, 0xF9u, 0xE1u, 0xF1u -> sbc(opcode.mode)
                0xC9u, 0xC5u, 0xD5u, 0xCDu, 0xDDu, 0xD9u, 0xC1u, 0xD1u -> cmp(opcode.mode)
                0xE0u, 0xE4u, 0xEcu -> cpx(opcode.mode)
                0xC0u, 0xC4u, 0xCCu -> cpy(opcode.mode)
                // inc & dec
                0xE6u, 0xF6u, 0xEEu, 0xFEu -> inc(opcode.mode)
                0xE8u -> inx() // x++
                0xC8u -> iny() // y++
                0xC6u, 0xD6u, 0xCeu, 0xDEu -> dec(opcode.mode)
                0xCAu -> dex()
                0x88u -> dey()
                // shift
                0x0Au, 0x06u, 0x16u, 0x0Eu, 0x1Eu -> asl(opcode.mode)
                0x4Au, 0x46u, 0x56u, 0x4Eu, 0x5Eu -> lsr(opcode.mode)
                0x2Au, 0x26u, 0x36u, 0x2Eu, 0x3Eu -> rol(opcode.mode)
                0x6Au, 0x66u, 0x76u, 0x6Eu, 0x7Eu -> ror(opcode.mode)
                // jump & calls
                0x4Cu, 0x6Cu -> jmp(opcode.mode)
                0x20u -> jsr(opcode.mode)
                0x60u -> rts()
                // branches
                0x90u -> if (!status[Flag.CARRY]) branch()
                0xB0u -> if (status[Flag.CARRY]) branch()
                0xD0u -> if (!status[Flag.ZERO]) branch()
                0xF0u -> if (status[Flag.ZERO]) branch()
                0x10u -> if (!status[Flag.NEGATIVE]) branch()
                0x30u -> if (status[Flag.NEGATIVE]) branch()
                0x50u -> if (!status[Flag.OVERFLOW]) branch()
                0x70u -> if (status[Flag.OVERFLOW]) branch()
                // status
                0x18u -> status[Flag.CARRY] = false
                0xD8u -> status[Flag.DECIMAL_MODE] = false
                0x58u -> status[Flag.INTERRUPT_DISABLE] = false
                0xB8u -> status[Flag.OVERFLOW] = false
                0x38u -> status[Flag.CARRY] = true
                0xF8u -> status[Flag.DECIMAL_MODE] = true
                0x78u -> status[Flag.INTERRUPT_DISABLE] = true
                // system functions: nop, brk, rti
                0xEAu -> {
                } // nop
                0x40u -> rti()
                0x00u -> {
                    brk()
                    return
                }
                else -> unreachable("unreachable code")
            }
            if (curPc == pc) {
                pc = (pc + opcode.len - 1u).toUShort()
            }
        }
    }
}
