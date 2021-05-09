package com.willendless.nes

import com.willendless.nes.emulator.Reg8bits
import org.junit.Assert
import org.junit.Test

@ExperimentalUnsignedTypes
class Reg8bitsTest {
    private val reg = Reg8bits()

    @Test fun test_inc() {
        Assert.assertEquals(1, reg.inc().getValUnsigned())
    }

    @Test fun test_dec() {
        Assert.assertEquals(255, reg.dec().getValUnsigned())
    }

    @Test fun test_plus() {
        Assert.assertEquals(42, reg.plus(42))
    }

    @Test fun test_plus_overflow() {
        Assert.assertEquals(42, reg.plus(42))
    }

    @Test fun test_plus_neg() {
        Assert.assertEquals(1, reg.inc().inc().plus(-1))
    }

    @Test fun test_plus_neg_overflow() {
        Assert.assertEquals(255, reg.plus(-1))
    }

    @Test fun test_set() {
        reg.set(42)
        Assert.assertEquals(42, reg.getValUnsigned())
    }

    @Test fun test_set_neg() {
        reg.set(-1)
        Assert.assertEquals(255, reg.getValUnsigned())
    }
}