package io.github.mionick.Math;

/**
 * Created by Nick on 5/15/2018.
 */

public class MathUtil {
    // Utility methods
    public static int intPower(int base, int power) {
        if (power < 0) {
            throw new IllegalArgumentException("Argument power must be >= 0");
        }

        int result = 1;
        for (int i = 0; i < power; i++) {
            result *= base;
        }
        return result;
    }

    // Java implementation of recursive
    // approach to find the number
    // of set bits in binary representation
    // of positive integer n
    // recursive function to count set bits
    public static int countSetBits(int n) {

        // base case
        if (n == 0)
            return 0;

        else

            // if last bit set add 1 else add 0
            return (n & 1) + countSetBits(n >>> 1);
    }


    // Copied from SO.
    // Only works when we already know the number passed in is a power of 2
    public static int log2(int n){
        if(n <= 0) throw new IllegalArgumentException();
        return 31 - Integer.numberOfLeadingZeros(n);
    }
}
