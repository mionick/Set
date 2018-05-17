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

    public int deckIndex = 0;

    public SetDeck(int features, int values) {
        // For a 3-ary set deck, 81 cards are possible. No duplicates.
        int uniqueNum = intPower(values, features);
        deck = new ArrayList<>(uniqueNum);

        // populate the deck with every unique card
        // need a features * features loop

        for (int j = 0; j < uniqueNum; j++) {
            // For each features we have to set the initial value.
            int[] initialValueArray = new int[features];
            // Here we are essentially converting base 10 into base 3, separating it and adding 1 to each digit
            // so 6 -> 0020 -> 1131
            for (int i  = 0; i < features; i ++) {
                initialValueArray[i] = ((j / intPower(values, i)) % values) + 1;
            }

            deck.add(new SetCard(initialValueArray, values));
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
        System.out.println("Returning card at index: " + deckIndex);
        deckIndex ++;
        return deck.get(deckIndex - 1);
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
        return deckIndex >= deck.size();
    }


}
