package io.github.mionick.set;

/**
 * This class is used mostly to represent the three selected cards by their indexes.
 */

public class IntTriple {

    private Integer int1 = null;
    private Integer int2 = null;
    private Integer int3 = null;

    public IntTriple() {
    }

    public IntTriple(int int1, int int2, int int3) {

        this.int1 = int1;
        this.int2 = int2;
        this.int3 = int3;
    }

    public Integer[] toArray() {
        return new Integer[]{
                int1, int2, int3
        };
    }

    public void clear() {
        int1 = null;
        int2 = null;
        int3 = null;
    }

    public boolean contains(int num) {
        return int1 == num || int2 == num || int3 == num;
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
