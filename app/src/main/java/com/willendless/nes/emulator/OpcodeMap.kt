package com.willendless.nes.emulator

import android.os.Build
import android.os.NetworkOnMainThreadException
import androidx.annotation.RequiresApi
import com.willendless.nes.emulator.AddressMode.*

data class Opcode(val code: Int, val name: String, val len: Int, val mode: AddressMode)

object OpcodeMap {
    private val opcodesList = arrayListOf<Opcode>(
            Opcode(0xe8, "INX", 1, NoneAddressing),
            // LDA
            Opcode(0xa9, "LDA", 2, Immediate),
            Opcode(0xa5, "LDA", 2, ZeroPage),
            Opcode(0xb5, "LDA", 2, ZeroPageX),
            Opcode(0xad, "LDA", 3, Absolute),
            Opcode(0xbd, "LDA", 3, AbsoluteX),
            Opcode(0xb9, "LDA", 3, AbsoluteY),
            Opcode(0xa1, "LDA", 2, IndirectX),
            Opcode(0xb1, "LDA", 2, IndirectY),
            // LDX
            Opcode(0xa2, "LDX", 2, Immediate),
            Opcode(0xa6, "LDX", 2, ZeroPage),
            Opcode(0xb6, "LDX", 2, ZeroPageY),
            Opcode(0xae, "LDX", 3, Absolute),
            Opcode(0xb3, "LDX", 3, AbsoluteY),
            // LDY
            Opcode(0xA0, "LDY", 2, Immediate),
            Opcode(0xA4, "LDY", 2, ZeroPage),
            Opcode(0xB4, "LDY", 2, ZeroPageX),
            Opcode(0xAc, "LDY", 3, Absolute),
            Opcode(0xBC, "LDY", 3, AbsoluteX),
            // STA
            Opcode(0x85, "STA", 2, ZeroPage),
            Opcode(0x95, "STA", 2, ZeroPageX),
            Opcode(0x8D, "STA", 3, Absolute),
            Opcode(0x9D, "STA", 3, AbsoluteX),
            Opcode(0x99, "STA", 3, AbsoluteY),
            Opcode(0x81, "STA", 2, IndirectX),
            Opcode(0x91, "STA", 2, IndirectY),
            // STX
            Opcode(0x86, "STX", 2, ZeroPage),
            Opcode(0x96, "STX", 2, ZeroPageY),
            Opcode(0x8E, "STX", 3, Absolute),
            // STY
            Opcode(0x84, "STY", 2, ZeroPage),
            Opcode(0x94, "STY", 2, ZeroPageX),
            Opcode(0x8C, "STY", 3, Absolute),
            // Register Transfer
            Opcode(0xaa, "TAX", 1, NoneAddressing),
            Opcode(0xA8, "TAY", 1, NoneAddressing),
            Opcode(0xBA, "TSX", 1, NoneAddressing),
            Opcode(0x8A, "TXA", 1, NoneAddressing),
            Opcode(0x9A, "TXS", 1, NoneAddressing),
            Opcode(0x98, "TYA", 1, NoneAddressing),
            // Stack push & pop
            Opcode(0x48, "PHA", 1, NoneAddressing),
            Opcode(0x08, "PHP", 1, NoneAddressing),
            Opcode(0x68, "PLA", 1, NoneAddressing),
            Opcode(0x28, "PLP", 1, NoneAddressing),
            // Logical Operations
            Opcode(0x29, "AND", 2, Immediate),
            Opcode(0x25, "AND", 2, ZeroPage),
            Opcode(0x35, "AND", 2, ZeroPageX),
            Opcode(0x2D, "AND", 3, Absolute),
            Opcode(0x3D, "AND", 3, AbsoluteX),
            Opcode(0x39, "AND", 3, AbsoluteY),
            Opcode(0x21, "AND", 2, IndirectX),
            Opcode(0x31, "AND", 2, IndirectY),
            Opcode(0x09, "ORA", 2, Immediate),
            Opcode(0x05, "ORA", 2, ZeroPage),
            Opcode(0x15, "ORA", 2, ZeroPageX),
            Opcode(0x0D, "ORA", 3, Absolute),
            Opcode(0x1D, "ORA", 3, AbsoluteX),
            Opcode(0x19, "ORA", 3, AbsoluteY),
            Opcode(0x01, "ORA", 2, IndirectX),
            Opcode(0x11, "ORA", 2, IndirectY),
            Opcode(0x49, "EOR", 2, Immediate),
            Opcode(0x45, "EOR", 2, ZeroPage),
            Opcode(0x55, "EOR", 2, ZeroPageX),
            Opcode(0x4D, "EOR", 3, Absolute),
            Opcode(0x5D, "EOR", 3, AbsoluteX),
            Opcode(0x59, "EOR", 3, AbsoluteY),
            Opcode(0x41, "EOR", 2, IndirectX),
            Opcode(0x51, "EOR", 2, IndirectY),
            Opcode(0x24, "BIT", 2, ZeroPage),
            Opcode(0x2C, "BIT", 3, Absolute),
            Opcode(0xC9, "CMP", 2, Immediate),
            Opcode(0xC5, "CMP", 2, ZeroPage),
            Opcode(0xD5, "CMP", 2, ZeroPageX),
            Opcode(0xCD, "CMP", 3, Absolute),
            Opcode(0xDD, "CMP", 3, AbsoluteX),
            Opcode(0xD9, "CMP", 3, AbsoluteY),
            Opcode(0xC1, "CMP", 2, IndirectX),
            Opcode(0xD1, "CMP", 2, IndirectY),
            Opcode(0xE0, "CPX", 2, Immediate),
            Opcode(0xE4, "CPX", 2, ZeroPage),
            Opcode(0xEC, "CPX", 3, Absolute),
            Opcode(0xC0, "CPY", 2, Immediate),
            Opcode(0xC4, "CPY", 3, ZeroPage),
            Opcode(0xCC, "CPY", 3, Absolute),
            // Arithmetic Operations
            Opcode(0x69, "ADC", 2, Immediate),
            Opcode(0x65, "ADC", 2, ZeroPage),
            Opcode(0x75, "ADC", 2, ZeroPageX),
            Opcode(0x6D, "ADC", 3, Absolute),
            Opcode(0x7D, "ADC", 3, AbsoluteX),
            Opcode(0x79, "ADC", 3, AbsoluteY),
            Opcode(0x61, "ADC", 2, IndirectX),
            Opcode(0x71, "ADC", 2, IndirectY),
            Opcode(0xE9, "SBC", 2, Immediate),
            Opcode(0xE5, "SBC", 2, ZeroPage),
            Opcode(0xF5, "SBC", 2, ZeroPageX),
            Opcode(0xED, "SBC", 3, Absolute),
            Opcode(0xFD, "SBC", 3, AbsoluteX),
            Opcode(0xF9, "SBC", 3, AbsoluteY),
            Opcode(0xE1, "SBC", 2, IndirectX),
            Opcode(0xF1, "SBC", 2, IndirectY),
            // Inc & Dec
            Opcode(0xE6, "INC", 2, ZeroPage),
            Opcode(0xF5, "INC", 2, ZeroPageX),
            Opcode(0xEE, "INC", 3, Absolute),
            Opcode(0xFE, "INC", 3, AbsoluteY),
            Opcode(0xE8, "INX", 1, NoneAddressing),
            Opcode(0xC8, "INY", 1, NoneAddressing),
            Opcode(0xC6, "DEC", 2, ZeroPage),
            Opcode(0xD6, "DEC", 2, ZeroPageX),
            Opcode(0xCE, "DEC", 3, Absolute),
            Opcode(0xDE, "DEC", 3, AbsoluteX),
            Opcode(0xCA, "DEX", 1, NoneAddressing),
            Opcode(0x88, "DEY", 1, NoneAddressing),
            // Shifts
            Opcode(0x0A, "ASL", 1, NoneAddressing),
            Opcode(0x06, "ASL", 2, ZeroPage),
            Opcode(0x16, "ASL", 2, ZeroPageX),
            Opcode(0x0E, "ASL", 3, Absolute),
            Opcode(0x1E, "ASL", 3, AbsoluteX),
            Opcode(0x4A, "LSR", 1, NoneAddressing),
            Opcode(0x46, "LSR", 2, ZeroPage),
            Opcode(0x56, "LSR", 2, ZeroPageX),
            Opcode(0x4E, "LSR", 3, Absolute),
            Opcode(0x5E, "LSR", 3, AbsoluteX),
            Opcode(0x2A, "ROL", 1, NoneAddressing),
            Opcode(0x26, "ROL", 2, ZeroPage),
            Opcode(0x36, "ROL", 2, ZeroPageX),
            Opcode(0x2E, "ROL", 3, Absolute),
            Opcode(0x3E, "ROL", 3, AbsoluteX),
            Opcode(0x6A, "ROR", 1, NoneAddressing),
            Opcode(0x66, "ROR", 2, ZeroPage),
            Opcode(0x76, "ROR", 2, ZeroPageX),
            Opcode(0x6E, "ROR", 3, Absolute),
            Opcode(0x7E, "ROR", 3, AbsoluteX),
            // Jumps & Calls
            Opcode(0x4C, "JMP", 3, Absolute),
            Opcode(0x6C, "JMP", 3, Indirect),
            Opcode(0x20, "JSR", 3, Absolute),
            Opcode(0x60, "RTS", 1, NoneAddressing),
            // Branches
            Opcode(0x90, "BCC", 2, NoneAddressing),
            Opcode(0xB0, "BCS", 2, NoneAddressing),
            Opcode(0xF0, "BEQ", 2, NoneAddressing),
            Opcode(0x30, "BMI", 2, NoneAddressing),
            Opcode(0xD0, "BNE", 2, NoneAddressing),
            Opcode(0x10, "BPL", 2, NoneAddressing),
            Opcode(0x50, "BVC", 2, NoneAddressing),
            Opcode(0x70, "BVS", 2, NoneAddressing),
            // Status Flag Changes
            Opcode(0x18, "CLC", 1, NoneAddressing),
            Opcode(0xD8, "CLD", 1, NoneAddressing),
            Opcode(0x58, "CLI", 1, NoneAddressing),
            Opcode(0xB8, "CLV", 1, NoneAddressing),
            Opcode(0x38, "SEC", 1, NoneAddressing),
            Opcode(0xF8, "SED", 1, NoneAddressing),
            Opcode(0x78, "SEI", 1, NoneAddressing),
            // System Functions
            Opcode(0x00, "BRK", 1, NoneAddressing),
            Opcode(0xEA, "NOP", 1, NoneAddressing),
            Opcode(0x40, "RTI", 1, NoneAddressing)
    )

    private val opcodes = HashMap<Int, Opcode>().apply {
        for (opcode in opcodesList) {
            this[opcode.code] = opcode
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun getOpcode(code: Int) = opcodes.getOrDefault(code, Opcode(0, "Unknown", 0, NoneAddressing))
}