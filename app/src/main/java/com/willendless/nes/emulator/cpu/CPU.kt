package com.willendless.nes.emulator.cpu

import com.willendless.nes.emulator.Bus
import com.willendless.nes.emulator.NESBus
import com.willendless.nes.emulator.util.unreachable
import java.io.PrintStream
import java.lang.StringBuilder

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
    var bus: Bus = NESBus

    private const val INT_VECTOR_NMI_BASE: UShort = 0xFFFAu
    private const val INT_VECTOR_RESET_BASE: UShort = 0xFFFCu
    private const val INT_VECTOR_BRK_BASE: UShort = 0xFFFEu

    fun switchBus(alternative: Bus) {
        bus = alternative
    }

    fun load(program: UByteArray, offset: Int) {
        // insert cartridge
        bus.populate(program, offset)
    }

    fun reset() {
        a = 0u
        x = 0u
        y = 0u
        sp = 0xfdu // See https://github.com/bugzmanov/nes_ebook/issues/10
        pc = bus.readUShort(INT_VECTOR_RESET_BASE)
        status(0u)
    }

    fun reset(entryPc: UShort) {
        a = 0u
        x = 0u
        y = 0u
        sp = 0xfdu // See https://github.com/bugzmanov/nes_ebook/issues/10
        pc = entryPc
        status(0u)
    }

    // Get current instruction byte without range check
    // and increment pc.
    // SIDE EFFECT: the value of pc will be incremented by 1.
    private fun fetchCode(): UByte = bus.readUByte(pc++)

    // Get current instruction byte without range check.
    // No side effect.
    private fun peekCode(): UByte = bus.readUByte(pc)

    private fun peekpeekCode(): UByte = bus.readUByte((pc + 1u).toUShort())

    // Return operand according to addressing mode.
    // No side effect to pc or any other registers.
    // REQUIRES: before called, pc should point to the second byte of the instruction.
    // ENSURES: return the memory address of the operand and InstExecContext may be modified
    private fun getOperandAddress(mode: AddressMode): UShort = when (mode) {
        AddressMode.Immediate -> pc
        AddressMode.ZeroPage -> peekCode().toUShort()
        AddressMode.ZeroPageX -> (peekCode() + x).toUByte().toUShort()  // zero page wrap around
        AddressMode.ZeroPageY -> (peekCode() + y).toUByte().toUShort()
        AddressMode.Absolute -> bus.readUShort(pc)
        AddressMode.AbsoluteX, AddressMode.AbsoluteY -> getAbsoluteOperandAddress(mode)
        AddressMode.Indirect -> pageReadUShort(bus.readUShort(pc))
        AddressMode.IndirectX -> pageReadUShort((peekCode() + x).toUByte().toUShort())
        AddressMode.IndirectY -> {
            val origin = pageReadUShort(peekCode().toUShort())
            val target = (pageReadUShort(peekCode().toUShort()) + y).toUShort()
            if (isPageCrossed(origin, target))
                InstExeContext.isPageCrossed = true
            target
        }
        AddressMode.Relative -> (pc.toInt() + peekCode().toByte() + 1).toUShort()
        AddressMode.Accumulator -> 0u
        AddressMode.NoneAddressing -> unreachable("$mode not supported")
    }

    private fun pageReadUShort(addr: UShort): UShort = if (addr.toUByte() == 0xFFu.toUByte()) {
        val lo = bus.readUByte(addr)
        val hi = bus.readUByte(addr xor 0xFFu)
        (lo + (hi.toUInt() shl 8)).toUShort()
    } else {
        bus.readUShort(addr)
    }

    // ENSURES: InstExecContext.isPageCrossed may be modified
    private fun getAbsoluteOperandAddress(mode: AddressMode): UShort {
        val origin = bus.readUShort(pc)
        val target = when (mode) {
            AddressMode.AbsoluteX -> (origin + x).toUShort()
            AddressMode.AbsoluteY -> (origin + y).toUShort()
            else -> unreachable("getAbsoluteOperandAddress() can only deal with" +
                    " AbsoluteX/Y addressing mode")
        }
        if (isPageCrossed(origin, target))
            InstExeContext.isPageCrossed = true
        return target
    }

    private fun isPageCrossed(origin: UShort, target: UShort): Boolean
        = ((origin and target) and 0xFFu) != 0u.toUShort()

    private fun lda(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = bus.readUByte(addr)
        PStatus.updateZN(a)
    }

    private fun ldx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        x = bus.readUByte(addr)
        PStatus.updateZN(x)
    }

    private fun ldy(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        y = bus.readUByte(addr)
        PStatus.updateZN(y)
    }

    private fun sta(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        bus.writeUByte(addr, a)
    }

    private fun stx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        bus.writeUByte(addr, x)
    }

    private fun sty(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        bus.writeUByte(addr, y)
    }

    private fun tax() {
        x = a
        PStatus.updateZN(x)
    }

    private fun tay() {
        y = a
        PStatus.updateZN(y)
    }

    private fun tsx() {
        x = sp
        PStatus.updateZN(x)
    }

    private fun txa() {
        a = x
        status.updateZN(a)
    }

    private fun txs() {
        sp = x
    }

    private fun tya() {
        a = y
        PStatus.updateZN(a)
    }

    private fun pha() {
        val addr = (0x100u + sp).toUShort()
        bus.writeUByte(addr, a)
        sp--
    }

    private fun php() {
        // https://wiki.nesdev.com/w/index.php/CPU_status_flag_behavior
        val addr = (0x100u + sp).toUShort()
        bus.writeUByte(
            addr,
            PStatus.toUByte() or Flag.BREAK.mask or Flag.BREAK2.mask
        )
        sp--
    }

    private fun pla() {
        val addr = (0x100u + ++sp).toUShort()
        a = bus.readUByte(addr)
        PStatus.updateZN(a)
    }

    private fun plp() {
        val addr = (0x100u + ++sp).toUShort()
        status.apply {
            this(bus.readUByte(addr))
            // TODO: why?
            setStatus(Flag.BREAK, false)
            setStatus(Flag.BREAK2, true)
        }
    }

    private fun and(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = a and bus.readUByte(addr)
        PStatus.updateZN(a)
    }

    private fun ora(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = a or bus.readUByte(addr)
        PStatus.updateZN(a)
    }

    private fun eor(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        a = a xor bus.readUByte(addr)
        PStatus.updateZN(a)
    }

    private fun bit(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = bus.readUByte(addr)
        status.apply {
            setStatus(Flag.OVERFLOW, (value and Flag.OVERFLOW.mask) != 0u.toUByte())
            setStatus(Flag.NEGATIVE, (value and Flag.NEGATIVE.mask) != 0u.toUByte())
            setStatus(Flag.ZERO, (a and value) == 0u.toUByte())
        }
    }

    private fun sax(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        bus.writeUByte(addr, a and x)
    }

    private fun adc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = bus.readUByte(addr)
        val sum = a + value + if (PStatus.getStatus(Flag.CARRY)) 1u else 0u
        status.apply {
            setStatus(Flag.CARRY, sum > UByte.MAX_VALUE)
            setStatus(
                Flag.OVERFLOW,
                (value.toUInt() xor sum)
                        and (a.toUInt() xor sum) and 0b1000_0000u != 0u
            )
        }
        a = sum.toUByte()
        PStatus.updateZN(a)
    }

    private fun sbc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val value = bus.readUByte(addr)
        val diff = a + (value xor 0xFFu) + if (PStatus.getStatus(Flag.CARRY)) 1u else 0u
        status.apply {
            setStatus(Flag.CARRY, diff > UByte.MAX_VALUE)
            setStatus(
                    Flag.OVERFLOW,
                    ((a xor value) and 0x80u.toUByte())
                        and ((a xor diff.toUByte()) and 0x80u.toUByte()) != 0u.toUByte()
            )
        }
        a = diff.toUByte()
        PStatus.updateZN(a)
    }

    private fun cmp(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val other = bus.readUByte(addr)
        status.apply {
            setStatus(Flag.CARRY, a >= other)
            updateZN((a - other).toUByte())
        }
    }

    private fun cpx(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        status.apply {
            setStatus(Flag.CARRY, x >= bus.readUByte(addr))
            updateZN((x - bus.readUByte(addr)).toUByte())
        }
    }

    private fun cpy(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        status.apply {
            setStatus(Flag.CARRY, y >= bus.readUByte(addr))
            updateZN((y - bus.readUByte(addr)).toUByte())
        }
    }

    private fun inc(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val res = (bus.readUByte(addr) + 1u).toUByte()
        bus.writeUByte(addr, res)
        PStatus.updateZN(res)
    }

    private fun inx() {
        x++
        PStatus.updateZN(x)
    }

    private fun iny() {
        y++
        PStatus.updateZN(y)
    }

    private fun dec(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        val res = (bus.readUByte(addr) - 1u).toUByte()
        bus.writeUByte(addr, res)
        PStatus.updateZN(res)
    }

    private fun dex() {
        x--
        PStatus.updateZN(x)
    }

    private fun dey() {
        y--
        PStatus.updateZN(y)
    }

    private fun asl(addressMode: AddressMode) {
        val value = when (addressMode) {
            AddressMode.Accumulator -> {
                PStatus.setStatus(Flag.CARRY, (a and 0b1000_0000u) != 0.toUByte())
                a = (a.toInt() shl 1).toUByte()
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                PStatus.setStatus(
                        Flag.CARRY,
                        (bus.readUByte(addr) and 0b1000_0000u) != 0.toUByte()
                )
                bus.writeUByte(addr, (bus.readUByte(addr).toUInt() shl 1).toUByte())
                bus.readUByte(addr)
            }
        }
        PStatus.updateZN(value)
    }

    private fun lsr(addressMode: AddressMode) {
        val value = when (addressMode) {
            AddressMode.Accumulator -> {
                PStatus.setStatus(Flag.CARRY, (a and 1u) != 0.toUByte())
                a = (a.toInt() shr 1).toUByte()
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                PStatus.setStatus(Flag.CARRY, (bus.readUByte(addr) and 1u) != 0.toUByte())
                bus.writeUByte(addr, (bus.readUByte(addr).toInt() shr 1).toUByte())
                bus.readUByte(addr)
            }
        }
        PStatus.updateZN(value)
    }

    private fun rol(addressMode: AddressMode) {
        val carry = PStatus.getStatus(Flag.CARRY)
        val value = when (addressMode) {
            AddressMode.Accumulator -> {
                PStatus.setStatus(Flag.CARRY, (a and 0b1000_0000u) != 0.toUByte())
                a = (a.toInt() shl 1).toUByte()
                if (carry) a = a or 0b0000_0001u
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                PStatus.setStatus(
                        Flag.CARRY,
                        (bus.readUByte(addr) and 0b1000_0000u) != 0.toUByte()
                )
                bus.writeUByte(addr, (bus.readUByte(addr).toInt() shl 1).toUByte())
                if (carry) bus.writeUByte(addr, bus.readUByte(addr) or 0b0000_0001u)
                bus.readUByte(addr)
            }
        }
        PStatus.updateZN(value)
    }

    private fun ror(addressMode: AddressMode) {
        val carry = PStatus.getStatus(Flag.CARRY)
        val value = when (addressMode) {
            AddressMode.Accumulator -> {
                PStatus.setStatus(Flag.CARRY, (a and 1u) != 0.toUByte())
                a = (a.toInt() shr 1).toUByte()
                if (carry) a = a or 0b1000_0000u
                a
            }
            else -> {
                val addr = getOperandAddress(addressMode)
                PStatus.setStatus(Flag.CARRY, (bus.readUByte(addr) and 1u) != 0.toUByte())
                bus.writeUByte(addr, (bus.readUByte(addr).toInt() shr 1).toUByte())
                if (carry) bus.writeUByte(addr, bus.readUByte(addr) or 0b1000_0000u)
                bus.readUByte(addr)
            }
        }
        PStatus.updateZN(value)
    }

    private fun jmp(addressMode: AddressMode) {
        val addr = getOperandAddress(addressMode)
        pc = addr
    }

    private fun jsr(addressMode: AddressMode) {
        val jumpAddr = getOperandAddress(addressMode)
        val retAddr = pc + 1u

//        printAddr("sp", sp.toInt())
//        printAddr("jumpAddr", jumpAddr.toInt())

        sp--
        bus.writeUShort((0x100u + sp).toUShort(), retAddr.toUShort())
        sp--
        pc = jumpAddr
    }

    private fun rts() {
        sp++
        val retAddr = bus.readUShort((0x100u + sp).toUShort()) + 1u
        sp++
//        printAddr("sp", sp.toInt())
//        printAddr("retAddr", retAddr.toInt())
        pc = retAddr.toUShort()
    }

    private fun printAddr(head: String, addr: Int) {
        println("$head addr: ${Integer.toHexString(addr)}")
    }

    // ENSURES: InstExecContext.isBranchSucceed will be modified and isPageCrossed may be modified
    private fun branch() {
        InstExeContext.isBranchSucceed = true
        val origin = pc
        pc = (pc.toInt() + peekCode().toByte() + 1).toUShort()
        if (isPageCrossed(origin, pc))
            InstExeContext.isPageCrossed = true
    }

    private fun rti() {
        sp++
        status((0b0010_0000u or bus.readUByte((sp + 0x100u).toUShort()).toUInt()).toUByte())
        sp++
        pc = bus.readUShort((sp + 0x100u).toUShort())
        sp++
    }

    private fun disAssemble(opcode: OpcodeMap.Opcode): String {
        val header = if (opcode.name[0] == '*') "" else " "
        val builder = if (opcode.mode == AddressMode.NoneAddressing) {
            return header + opcode.name
        } else {
            StringBuilder(header).append(opcode.name).append(" ")
        }

        val a1: Int = peekCode().toInt()
        val a2: Int = peekpeekCode().toInt()
        val address: UShort = getOperandAddress(opcode.mode)
        val operandUByte: Int = bus.readUByte(address).toInt()
        val operandUShort: Int = bus.readUShort(address).toInt()
        val addr: Int = address.toInt()
        when (opcode.mode) {
            AddressMode.Immediate -> builder.append("#$").append("%02X".format(a1))
            AddressMode.ZeroPage -> builder.append("$%02X ".format(a1))
                    .append("= %02X".format(operandUByte))
            AddressMode.ZeroPageX -> builder.append("$%02X".format(a1))
                    .append(",X ").append("@ %02X = %02X".format(addr, operandUByte))
            AddressMode.ZeroPageY -> builder.append("$%02X".format(a1))
                    .append(",Y ").append("@ %02X = %02X".format(addr, operandUByte))
            AddressMode.Absolute -> {
                builder.append("$%04X".format(addr))
                 if (opcode.name != "JMP" && opcode.name != "JSR") builder.append(" = %02X".format(operandUByte))
            }
            AddressMode.AbsoluteX -> builder.append("$%02X%02X".format(a2, a1))
                    .append(",X ").append("@ %04X = %02X".format(addr, operandUByte))
            AddressMode.AbsoluteY -> builder.append("$%02X%02X".format(a2, a1))
                    .append(",Y ").append("@ %04X = %02X".format(addr, operandUByte))
            AddressMode.Indirect -> {
                builder.append("($%02X%02X) ".format(a2, a1))
                if (opcode.name == "JMP") builder.append("= %04X".format(addr))
                else builder.append("= %04X".format(operandUShort))
            }
            AddressMode.IndirectX -> builder.append("($%02X,X) ".format(a1))
                    .append("@ %02X = %04X = %02X".format((x + peekCode()).toUByte().toInt(), addr, operandUShort))
            AddressMode.IndirectY -> builder.append("($%02X),Y ".format(a1))
                    .append("= %04X @ %04X = %02X".format(pageReadUShort(peekCode().toUShort()).toInt(), addr, operandUShort))
            AddressMode.Relative -> builder.append("$%04X".format(addr))
            AddressMode.Accumulator -> builder.append("A")
            AddressMode.NoneAddressing -> unreachable("code should not reach here")
        }
        return builder.toString()
    }

    private fun traceCPUState(opcode: OpcodeMap.Opcode, os: PrintStream) {
        os.print("%04X  %02X ".format(pc.toInt() - 1, opcode.code.toInt()))
        when (opcode.len.toInt()) {
            1 -> os.print("%6c".format(' '))
            2 -> os.print("%02X %3c".format(peekCode().toInt(), ' '))
            3 -> os.print("%02X %02X ".format(peekCode().toInt(), peekpeekCode().toInt()))
            else -> unreachable("invalid opcode length")
        }
        os.print("%-33s".format(disAssemble(opcode)))
        os.println("A:%02X X:%02X Y:%02X P:%02X SP:%02X".format(a.toInt(), x.toInt(), y.toInt(), status.get().toInt(), sp.toInt()))
    }

    // save cpu context and jump to BRK handler
    private fun brk() {
        sp--
        bus.writeUShort((sp + 0x100u).toUShort(), pc)
        sp--
        bus.writeUByte((sp + 0x100u).toUShort(), PStatus.toUByte() or 0b0011_0000u)
        sp--
        pc = bus.readUShort(INT_VECTOR_BRK_BASE)
        PStatus.setStatus(Flag.INTERRUPT_DISABLE, true)
    }

    // save cpu context and jump to NMI handler
    private fun interruptNMI() {
        sp--
        bus.writeUShort((sp + 0x100u).toUShort(), pc)
        sp--
        bus.writeUByte((sp + 0x100u).toUShort(),
            status.get() or Flag.BREAK.mask or Flag.BREAK2.mask)
        sp--
        status.setStatus(Flag.INTERRUPT_DISABLE, true)
        bus.tick(2)
        pc = bus.readUShort(INT_VECTOR_NMI_BASE)
    }

    private object InstExeContext {
        var isBranchSucceed: Boolean = false
        var isPageCrossed: Boolean = false

        fun set(isBranchSucceed: Boolean, isPageCrossed: Boolean) {
            this.isBranchSucceed = isBranchSucceed
            this.isPageCrossed = isPageCrossed
        }
    }

    // Run game in the memory begin from 0x8000.
    // For each instruction, check NMI interrupt before interpreted each instruction
    fun run(timeoutMs: Long = 10_000, maxStep: Long = 10_000, os: PrintStream? = null) {
        var curStep = 0
        val startTime = System.nanoTime() / 1000_000
        while (true) {
            val curTime = System.nanoTime() / 1000_000
            if (curTime - startTime > timeoutMs || curStep >= maxStep)
                break

            curStep += 1

            // Handle NMI interrupt before interpret instructions
            if (bus.pollNMIStatus()) {
                interruptNMI()
            }

            val code = fetchCode()
            val opcode = OpcodeMap.getOpcode(code)
            val curPc = pc

            // reset current instruction execution context
            InstExeContext.set(false, false)

            // TODO: maybe don't increase pc in `fetchCode`?
            if (os != null) traceCPUState(opcode, os)

            // TODO: reflection with `getDeclaredField`?
            when (opcode.code.toUInt()) {
                // load/store
                0xA9u, 0xA5u, 0xB5u, 0xADu, 0xBDu, 0xB9u, 0xA1u, 0xB1u -> lda(opcode.mode)
                0xA2u, 0xA6u, 0xB6u, 0xAEu, 0xBEu -> ldx(opcode.mode)
                0xA0u, 0xA4u, 0xB4u, 0xACu, 0xBCu -> ldy(opcode.mode)
                0x85u, 0x95u, 0x8Du, 0x9Du, 0x99u, 0x81u, 0x91u -> sta(opcode.mode)
                0x86u, 0x96u, 0x8Eu -> stx(opcode.mode)
                0x84u, 0x94u, 0x8Cu -> sty(opcode.mode)
                0xA7u, 0xB7u, 0xAFu, 0xBFu, 0xA3u, 0xB3u -> {
                    lda(opcode.mode)
                    tax()
                }
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
                // bit
                0x29u, 0x25u, 0x35u, 0x2Du, 0x3Du, 0x39u, 0x21u, 0x31u -> and(opcode.mode)
                0x09u, 0x05u, 0x15u, 0x0Du, 0x1Du, 0x19u, 0x01u, 0x11u -> ora(opcode.mode)
                0x49u, 0x45u, 0x55u, 0x4Du, 0x5Du, 0x59u, 0x41u, 0x51u -> eor(opcode.mode)
                0x24u, 0x2Cu -> bit(opcode.mode)
                0x87u, 0x97u, 0x83u, 0x8Fu -> sax(opcode.mode)
                // arithmetic
                0x69u, 0x65u, 0x75u, 0x6Du, 0x7Du, 0x79u, 0x61u, 0x71u -> adc(opcode.mode)
                0xE9u, 0xE5u, 0xF5u, 0xEDu, 0xFDu, 0xF9u, 0xE1u, 0xF1u, 0xEBu -> sbc(opcode.mode)
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
                0xC7u, 0xD7u, 0xCFu, 0xDFu, 0xDBu, 0xC3u, 0xD3u -> {
                    dec(opcode.mode)
                    cmp(opcode.mode)
                }
                0xE7u, 0xF7u, 0xEFu, 0xFFu, 0xFBu, 0xE3u, 0xF3u -> {
                    inc(opcode.mode)
                    sbc(opcode.mode)
                }
                // shift
                0x0Au, 0x06u, 0x16u, 0x0Eu, 0x1Eu -> asl(opcode.mode)
                0x4Au, 0x46u, 0x56u, 0x4Eu, 0x5Eu -> lsr(opcode.mode)
                0x2Au, 0x26u, 0x36u, 0x2Eu, 0x3Eu -> rol(opcode.mode)
                0x6Au, 0x66u, 0x76u, 0x6Eu, 0x7Eu -> ror(opcode.mode)
                0x07u, 0x17u, 0x0Fu, 0x1Fu, 0x1Bu, 0x03u, 0x13u -> {
                    asl(opcode.mode)
                    ora(opcode.mode)
                }
                0x27u, 0x37u, 0x2Fu, 0x3Fu, 0x3Bu, 0x23u, 0x33u -> {
                    rol(opcode.mode)
                    and(opcode.mode)
                }
                0x47u, 0x57u, 0x4Fu, 0x5Fu, 0x5Bu, 0x43u, 0x53u -> {
                    lsr(opcode.mode)
                    eor(opcode.mode)
                }
                0x67u, 0x77u, 0x6Fu, 0x7Fu, 0x7Bu, 0x63u, 0x73u -> {
                    ror(opcode.mode)
                    adc(opcode.mode)
                }
                // jump & calls
                0x4Cu, 0x6Cu -> jmp(opcode.mode)
                0x20u -> jsr(opcode.mode)
                0x60u -> rts()
                // branches
                0x90u -> if (!PStatus.getStatus(Flag.CARRY)) branch()
                0xB0u -> if (PStatus.getStatus(Flag.CARRY)) branch()
                0xD0u -> if (!PStatus.getStatus(Flag.ZERO)) branch()
                0xF0u -> if (PStatus.getStatus(Flag.ZERO)) branch()
                0x10u -> if (!PStatus.getStatus(Flag.NEGATIVE)) branch()
                0x30u -> if (PStatus.getStatus(Flag.NEGATIVE)) branch()
                0x50u -> if (!PStatus.getStatus(Flag.OVERFLOW)) branch()
                0x70u -> if (PStatus.getStatus(Flag.OVERFLOW)) branch()
                // status
                0x18u -> PStatus.setStatus(Flag.CARRY, false)
                0xD8u -> PStatus.setStatus(Flag.DECIMAL_MODE, false)
                0x58u -> PStatus.setStatus(Flag.INTERRUPT_DISABLE, false)
                0xB8u -> PStatus.setStatus(Flag.OVERFLOW, false)
                0x38u -> PStatus.setStatus(Flag.CARRY, true)
                0xF8u -> PStatus.setStatus(Flag.DECIMAL_MODE, true)
                0x78u -> PStatus.setStatus(Flag.INTERRUPT_DISABLE, true)
                // system functions: nop, brk, rti
                // nop without operand
                0xEAu, 0x1Au, 0x3Au, 0x5Au, 0x7Au, 0xDAu, 0xFAu,
                0x04u, 0x14u, 0x34u, 0x44u, 0x54u, 0x64u, 0x74u,
                0x80u, 0x82u, 0x89u, 0xC2u, 0xD4u, 0xE2u, 0xF4u, 0x0Cu -> {}
                // nop with operand and may influenced InstExecContext
                0x1Cu, 0x3Cu, 0x5Cu, 0x7Cu, 0xDCu, 0xFCu -> getOperandAddress(opcode.mode)
                0x40u -> rti()
                0x00u -> brk()
                else -> unreachable("unreachable code")
            }

            var cycles = opcode.cycles
            if (opcode.pageCrossedOverhead && InstExeContext.isPageCrossed) cycles++
            if (opcode.branch && InstExeContext.isBranchSucceed) cycles++
            bus.tick(cycles)

            if (curPc == pc) {
                pc = (pc + opcode.len - 1u).toUShort()
            }
        }
    }
}
