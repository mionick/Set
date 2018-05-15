package io.github.mionick.set;

import android.support.annotation.NonNull;

import io.github.mionick.cards.Card;

import static io.github.mionick.Math.MathUtil.*;

/**
 * Created by Nick on 5/15/2018.
 */

public class SetCard implements Card {
    // A Set Card is just an n-ary vector
    // We will represent each dimension as a byte
    // So the card can be represented by an array of bytes

    private final byte[] array;
    private final int dimension;

    public SetCard(int dimension) {
        this.dimension = dimension;
        this.array = new byte[dimension];
    }


    public SetCard(int[] initialValues) {
        // initialValues is expected to be a list of values at least as long as the dimension of the card.
        if (initialValues == null) {
            throw new NullPointerException("Cannot create card with unknown dimension");
        }

        this.dimension = initialValues.length;
        this.array = new byte[this.dimension];

        // Initialize based on initial values.
        // We convert an int into the bit that represents that value
        for (int i = 0; i < dimension; i++) {
            this.array[i] = (byte) (1 << (initialValues[i] - 1));
        }
    }

    public byte getByteValue(int dimension) {
        return this.array[dimension];
    }
    public int getIntValue(int dimension) {
        return log2(this.array[dimension]);
    }

    public int setIntValue(int dimension, int value) {
        return this.array[dimension] = (byte) (1 << (value -1));
    }

}

