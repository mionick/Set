package io.github.mionick.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

import static io.github.mionick.Math.MathUtil.countSetBits;

/**
 * This class will contain the main game logic.
 * This includes the currently displayed cards,
 * the logic to see if three cards are a set,
 * And the behavior when a set is found.
 *
 * This will only play a game of 3Ary set for now.
 *
 * Created by Nick on 5/15/2018.
 */

public class SetGame {

    private static final int DEFAULT_BOARD_SIZE = 12;

    SetDeck deck = new SetDeck(4, 3);

    ArrayList<Integer> hint = new ArrayList<>();

    // 21 is the maximum number of cards that can be on the field without there being a set.
    private SetCard[] currentlyDisplayedCards;
    private int currentBoardSize;

    /**
     * The constructor of the game guarantees that there is a set at the start.
     */
    public SetGame() {

        currentlyDisplayedCards = new SetCard[21];

        currentBoardSize = DEFAULT_BOARD_SIZE;

        // initialize the first twelve spots.
        do {
            deck.shuffle();
            // Start by Drawing 12 Cards
            for (int i = 0; i < currentBoardSize; i++) {
                currentlyDisplayedCards[i] = deck.Draw();
            }
        }
        // We want to guarantee we start with a set present.
        // It's no fun to start with more than 12 cards.
        while (!isSetPresent());
        EnsurePlayable();

    }

    // Getters:
    public SetCard[] getBoard() {
        return currentlyDisplayedCards;
    }

    public int getCurrentBoardSize() {
        return currentBoardSize;
    }



    /*
    Game Loop:

    while (there is a set)

    Wait for selection of three cards

    If they are a set,
        remove them and try to add three more.
        Check if there is a set
        If there is no set,
            if the deck has cards then add more cards.
            If the deck is empty, the game is over.
    If they are not a set,
        Minus one point.


     */

    // TODO: Measure performance. If it's an issue with more than 12 cards, we can get away with only checking the added cards.
    public boolean isSetPresent() {
        // Need to check every possible combination of three cards to see if any sets exist.
        // Can short circuit once one is found.
        hint.clear();

        // Need three counters
        for (int i = 0; i < currentBoardSize - 2; i++ ) {
            for (int j = i +1; j < currentBoardSize - 1; j++) {
                for (int k = j+1; k < currentBoardSize; k++) {
                    if (isSet(i, j, k)) {
                        hint.add(i);
                        hint.add(j);
                        hint.add(k);
                        return true;
                    }
                }
            }
        }

        // If we loop through all cards on the board, there is not set.
        return false;
    }


    public boolean isSet(int index1, int index2, int index3) {
        return isSet(
                currentlyDisplayedCards[index1],
                currentlyDisplayedCards[index2],
                currentlyDisplayedCards[index3]
        );
    }

    public void SelectCards(int ... chosenCards) {

        Arrays.sort(chosenCards);

        if (isSet(
                chosenCards[0],
                chosenCards[1],
                chosenCards[2]
        )) {
            // TODO: Throw Set Found Event
            /*
            remove them and try to add three more.
                Check if there is a set
                If there is no set,
                if the deck has cards then add more cards.
                If the deck is empty, the game is over.
             */

            // Set these indices to null.
            // For each one that's not null, replace one of the chosen indexes.
            for (int i : chosenCards) {
                currentlyDisplayedCards[i] = null;
            }

            // If the board size was more than twelve, then we do not need to add new cards
            if (currentBoardSize > DEFAULT_BOARD_SIZE) {

                // TODO: How the hell am I going to animate this.
                // Need an event for Card moving
                // Loop through the last three items
                int indexToReplace = 0;
                for (int i  = 1; i < 4; i++) {
                    if (currentlyDisplayedCards[currentBoardSize - i] != null) {
                        currentlyDisplayedCards[chosenCards[indexToReplace]] = currentlyDisplayedCards[currentBoardSize - i];
                        indexToReplace ++;
                    }
                }

                currentBoardSize -= 3;
                // There should be no null values in the playable area at this point.
                for (int i  = 0; i < currentBoardSize; i++) {
                    assert(currentlyDisplayedCards[i] != null);
                }


            } else {
                // We have to try to add more cards
                if (!deck.isEmpty()) {
                    // We only draw cards three at a time, so this is safe
                    currentlyDisplayedCards[chosenCards[0]] = deck.Draw();
                    currentlyDisplayedCards[chosenCards[1]] = deck.Draw();
                    currentlyDisplayedCards[chosenCards[2]] = deck.Draw();
                }
            }


            EnsurePlayable();

        } else {
            // TODO: Throw not a set event
        }
    }

    private void EnsurePlayable() {

        while (!isSetPresent()) {
            if (deck.isEmpty()) {
                // TODO: Game is over
                System.out.println("GAME IS OVER. DECK IS EMPTY, and NO SET.");
                OnGameOver(0);
                return;
            } else {
                // Add more cards
                // TODO: Throw cards added event, the view might have to adjust
                currentlyDisplayedCards[currentBoardSize] = deck.Draw();
                currentlyDisplayedCards[currentBoardSize + 1] = deck.Draw();
                currentlyDisplayedCards[currentBoardSize + 2] = deck.Draw();
                currentBoardSize += 3;
            }
        }
    }

    // ============================ EVENT HOOKS =============================


    // No Set Available
    public interface INoSetHandler {
        void AddingCards(SetCard ... cards);
    }

    private ArrayList<INoSetHandler> noSetHandlers = new ArrayList<>();
    public void AddNoSetHander(INoSetHandler handler) {
        noSetHandlers.add(handler);
    }
    private void NoSetAvailable(SetCard ... cards) {
        for (INoSetHandler noSetHandler: noSetHandlers) {
            noSetHandler.AddingCards(cards);
        }
    }

    // Cards Selected TODO: This event handler should be in the view, then call select cards here
    public interface ICardsSelectedHandler {
        void SelectCards(int ... cards);
    }

    private ArrayList<ICardsSelectedHandler> cardsSelectedHandlers = new ArrayList<>();
    public void AddCardsSelectedHandler(ICardsSelectedHandler handler) {
        cardsSelectedHandlers.add(handler);
    }
    private void CardSelected(int ... cards) {
        for (ICardsSelectedHandler cardsSelectedHandler: cardsSelectedHandlers) {
            cardsSelectedHandler.SelectCards(cards);
        }
    }

    // Game Over
    public interface IGameOverHandler {
        // parameter to be used later for stats like sets/second
        void OnGameOver(int numberOfSets);
    }

    private ArrayList<IGameOverHandler> gameOverHandlers = new ArrayList<>();
    public void AddGameOverHandler(IGameOverHandler handler) {
        gameOverHandlers.add(handler);
    }
    private void OnGameOver(int numSets) {
        for (IGameOverHandler handler: gameOverHandlers) {
            handler.OnGameOver(numSets);
        }
    }

    // =========================== STATIC METHODS ===========================
    // Behaviour is undefined if cards of a different cardinality are passed in.
    public static boolean isSet(SetCard ... cards) {
        if (cards == null) {
            throw new IllegalArgumentException("cards cannot be null");
        }

        // base case is true
        int numberOfCards = cards.length;
        if (numberOfCards <= 1) {
            return true;
        }

        boolean result = true;

        // Short circuit once we find out this isn't a set in one numberOfCards.
        // Loop through number fo features on each card
        for (int i = 0; i < 4  && result; i++) {

            byte intermediateOrResult = 0;
            for (SetCard card : cards) {

                // If any of the cards are null, this is not a set.
                // This happens while the AI searches for all possible combinations.
                if (card == null) {
                    return false;
                }

                if (card.getByteValue(i) == 0) {
                    System.out.println("Issue with card index: " + card);
                }
                intermediateOrResult  |= card.getByteValue(i);
            }

            // Intermediate result should be true if every card has a different value in this numberOfCards,
            // or if every card has the same value in this numberOfCards.

            // This equates to the OR result having one 1 bit = all the same
            // or the OR result having a {numberOfCards} on bits, meaning they were all different.

            int numSet = countSetBits(intermediateOrResult);
            result = ( numSet == 1 || numSet == numberOfCards);


        }

        return result;

    }

}
