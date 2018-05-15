package io.github.mionick.set;

/**
 * Unit Tests for Math functions I need.
 * Created by Nick on 5/15/2018.
 */
import org.junit.Test;

import static io.github.mionick.Math.MathUtil.*;
import static junit.framework.Assert.assertEquals;

public class MathUtilTest {

    // intPower tests
    @Test
    public void intPowerReturnsCorrectNumber() {
        assertEquals(4,intPower(2, 2));
    }
    @Test
    public void intPowerReturns1() {
        assertEquals(1,intPower(100, 0));
    }
    @Test(expected = IllegalArgumentException.class)
    public void intPowerThrowsWithNegativePower() {
        intPower(2, -1);
    }

    // countSetBits tests
    @Test
    public void countSetBitsReturnsCorrectNumber() {
        assertEquals(1,countSetBits(2));
        assertEquals(2,countSetBits(3));
        assertEquals(0,countSetBits(0));
    }
    @Test
    public void countSetBitsReturns32ForMinus1() {
        assertEquals(32,countSetBits(-1));
    }

    // log2 tests
    @Test
    public void log2ReturnsCorrectNumberForPowerOf2() {
        assertEquals(0,log2(1));
        assertEquals(1,log2(2));
        assertEquals(2,log2(4));
    }
}
