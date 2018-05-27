package io.github.mionick.set;

/**
 * This class is used mostly to represent the three selected cards by their indexes.
 */

public class IntTriple {

    private int int1;
    private int int2;
    private int int3;

    public IntTriple(int int1, int int2, int int3) {

        this.int1 = int1;
        this.int2 = int2;
        this.int3 = int3;
    }

    public int[] toArray() {
        return new int[]{
                int1, int2, int3
        };
    }

    public int getInt1() {
        return int1;
    }

    public void setInt1(int int1) {
        this.int1 = int1;
    }

    public int getInt2() {
        return int2;
    }

    public void setInt2(int int2) {
        this.int2 = int2;
    }

    public int getInt3() {
        return int3;
    }

    public void setInt3(int int3) {
        this.int3 = int3;
    }
}
