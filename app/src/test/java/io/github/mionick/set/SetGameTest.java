package io.github.mionick.set;

/**
 *
 * Unit tests for the Game
 *
 * Created by Nick on 5/15/2018.
 *
 */

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class SetGameTest {
    @Test
    public void isSetCorrectlyIdentifiesSet1Different() {
        SetCard card1 = new SetCard(new int[]{1, 1, 1});
        SetCard card2 = new SetCard(new int[]{1, 1, 2});
        SetCard card3 = new SetCard(new int[]{1, 1, 3});
        assertEquals(true, SetGame.isSet(card1, card2, card3));
    }

    @Test
    public void isSetCorrectlyIdentifiesSetAllDiff() {
        SetCard card1 = new SetCard(new int[]{1, 1, 1});
        SetCard card2 = new SetCard(new int[]{2, 2, 2});
        SetCard card3 = new SetCard(new int[]{3, 3, 3});
        assertEquals(true, SetGame.isSet(card1, card2, card3));
    }

    @Test
    public void isSetCorrectlyIdentifiesNotSet() {
        SetCard card1 = new SetCard(new int[]{1, 1, 1});
        SetCard card2 = new SetCard(new int[]{1, 1, 1});
        SetCard card3 = new SetCard(new int[]{1, 1, 3});
        assertEquals(false, SetGame.isSet(card1, card2, card3));
    }

    @Test
    public void isSetCorrectlyIdentifiesNotSetAllDiffTwoRepeats() {
        SetCard card1 = new SetCard(new int[]{1, 1, 1});
        SetCard card2 = new SetCard(new int[]{2, 3, 1});
        SetCard card3 = new SetCard(new int[]{3, 2, 3});
        assertEquals(false, SetGame.isSet(card1, card2, card3));
    }
}
