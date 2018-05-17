package io.github.mionick.set;

import io.github.mionick.cards.Card;

import static io.github.mionick.Math.MathUtil.*;

/**
 * Created by Nick on 5/15/2018.
 */

public class SetCard implements Card {
    // A Set Card is just an nxm-ary vector
    // n is number of features, m is possible values for each.
    // We will represent each features as a byte
    // number of possible values determines how many cards are needed for a set.
    // So the card can be represented by an array of bytes

    private final byte[] array;
    private final int features;
    private final int values;

    public SetCard(int features, int values) {
        this.features = features;
        this.values = values;
        this.array = new byte[features];
    }


    public SetCard(int[] initialValues, int values) {
        // initialValues is expected to be a list of values at least as long as the features of the card.
        // initial values should all be greater than 0, and <= values
        if (initialValues == null) {
            throw new NullPointerException("Cannot create card with unknown features");
        }

        for (int i : initialValues) {
            if (i == 0  || i > values) {
                throw new IllegalArgumentException("Initial Values should be in the range [1, values]");
            }
        }

        this.values = values;
        this.features = initialValues.length;
        this.array = new byte[this.features];

        // Initialize based on initial values.
        // We convert an int into the bit that represents that value
        for (int i = 0; i < features; i++) {
            setIntValue(i, initialValues[i]);
        }
    }

    public byte getByteValue(int feature) {
        return this.array[feature];
    }
    public int getIntValue(int feature) {
        return log2(this.array[feature]);
    }
    public int getNumPossibleValues() {
        return values;
    }
    public int getNumFeatures() {
        return features;
    }

    public int setIntValue(int feature, int value) {
        return this.array[feature] = (byte) (1 << (value -1));
    }


    @Override
    public String toString() {
        String result = "[";
        for (byte b : this.array) {
            result += " " + Integer.toBinaryString(b);
        }

        return result + " ]";
    }

}

