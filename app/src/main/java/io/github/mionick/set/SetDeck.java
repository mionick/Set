package io.github.mionick.set;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.IntStream;

import io.github.mionick.cards.CardDeck;

import static io.github.mionick.Math.MathUtil.*;

/**
 * Created by Nick on 5/15/2018.
 */

public class SetDeck extends CardDeck<SetCard> {

    private final ArrayList<SetCard> deck;

    private int deckIndex = 0;

    public SetDeck(int dimension) {
        // For a 3-ary set deck, 81 cards are possible. No duplicates.
        int uniqueNum = intPower(dimension, dimension);
        deck = new ArrayList<>(uniqueNum);

        // populate the deck with every unique card
        // need a dimension * dimension loop

        for (int j = 0; j < uniqueNum; j++) {
            // For each dimension we have to set the initial value.
            int[] initialValueArray = new int[dimension];
            for (int i  = 1; i <= dimension; i ++) {
                initialValueArray[i] = j % intPower(dimension, i);
            }

            deck.add(new SetCard(initialValueArray));
        }

    }

    public void shuffle() {
        deckIndex = 0;
        Collections.shuffle(deck);
    }

    public SetCard Draw() {
        if (isEmpty()) {
            return null;
        }
        return deck.get(deckIndex++);
    }

    public SetCard DrawAndLoop() {
        if (isEmpty()) {
            shuffle();
        }
        return deck.get(deckIndex++);
    }

    // A deck with 1 card
    // size == 1
    // after drawing once, deckindex will equal size
    public boolean isEmpty() {
        return deck.size() >= deckIndex;
    }


}
