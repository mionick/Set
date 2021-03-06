package io.github.mionick.set;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import io.github.mionick.events.EventHandlerSet;
import io.github.mionick.events.EventInstance;

import static io.github.mionick.math.MathUtil.countSetBits;

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
    public SetDeck deck;

    private IntTriple hint = new IntTriple();

    // 21 is the maximum number of cards that can be on the field without there being a set.
    private SetCard[] currentlyDisplayedCards = new SetCard[21];
    private int currentBoardSize = DEFAULT_BOARD_SIZE;
    private long seed;
    private int numberOfSetsFound;
    private boolean gameIsOver = false;

    // Events During the Game
    // public so others can add event handlers to catch event
    public EventHandlerSet<SetGameEvent> eventHandlerSet = new EventHandlerSet<>(SetGameEvent.class);


    /**
     * The constructor of the game guarantees that there is a set at the start.
     */
    // This constructor is used when we don't care what seed is used.
    public SetGame() {
        this(System.nanoTime());
        // Something else must tell us when we can start the game. Until then we should not populate the cards though.
    }


    // This can be used to replay a game that a friend played, to try to beat their score.
    public SetGame(long randomSeed) {

        // If we are passed a seed, everything should use that.
        Random random = new Random();
        random.setSeed(randomSeed);
        this.seed = randomSeed;

        deck = new SetDeck(4, 3, random);
        deck.shuffle();


    }
    // This can be used to replay a game that a friend played, to try to beat their score.
    public SetGame(SetDeck deck) {
        this.deck = deck;
    }

    public IntTriple getHint() {
        return hint;
    }

    public void startGame() {

        // initialize the first twelve spots.
        do {
            // Start by Drawing 12 Cards
            for (int i = 0; i < currentBoardSize; i++) {
                currentlyDisplayedCards[i] = deck.Draw();
            }
        }
        // We want to guarantee we start with a set present.
        // It's no fun to start with more than 12 cards.
        while (!isSetPresent());
        EnsurePlayable();

        eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.GAME_START, deck));
    }

    // Getters:
    public SetCard[] getBoard() {
        return currentlyDisplayedCards;
    }

    public int getCurrentBoardSize() {
        return currentBoardSize;
    }

    public boolean isSetPresent() {
        // Need to check every possible combination of three cards to see if any sets exist.
        // Can short circuit once one is found.
        hint.clear();

        // Need three counters
        for (int i = 0; i < currentBoardSize - 2; i++ ) {
            for (int j = i +1; j < currentBoardSize - 1; j++) {
                for (int k = j+1; k < currentBoardSize; k++) {
                    if (isSet(i, j, k)) {
                        hint.setInt1(i);
                        hint.setInt2(j);
                        hint.setInt3(k);
                        return true;
                    }
                }
            }
        }

        // If we loop through all cards on the board, there is not set.
        eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.NO_SETS));
        return false;
    }


    public boolean isSet(int index1, int index2, int index3) {
        boolean result =  isSet(
                currentlyDisplayedCards[index1],
                currentlyDisplayedCards[index2],
                currentlyDisplayedCards[index3]
        );
        return result;
    }

    public void SelectCards(String source, Integer ... chosenCards) {

        if (gameIsOver) {
            return;
        }

        Arrays.sort(chosenCards);

        if (isSet(
                chosenCards[0],
                chosenCards[1],
                chosenCards[2]
        )) {
            eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(
                    SetGameEvent.CORRECT_SET,
                    source,
                    new SetCard[]{
                            currentlyDisplayedCards[chosenCards[0]],
                            currentlyDisplayedCards[chosenCards[1]],
                            currentlyDisplayedCards[chosenCards[2]]
                    },
                    chosenCards.clone()
                    ));
            /*
            remove them and try to add three more.
                Check if there is a set
                If there is no set,
                if the deck has cards then add more cards.
                If the deck is empty, the game is over.
             */

            numberOfSetsFound ++;

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
                    eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.CARDS_ADDED,
                            // Need to wrap this in an object array otherwise the array of cards is expanded.
                            new Object[] {
                                new SetCard[]{
                                            currentlyDisplayedCards[chosenCards[0]],
                                            currentlyDisplayedCards[chosenCards[1]],
                                            currentlyDisplayedCards[chosenCards[2]]
                                    }
                            }
                    ));
                }
            }

            EnsurePlayable();

        } else {
            eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.INCORRECT_SET,
                    source,
                    new SetCard[]{
                            currentlyDisplayedCards[chosenCards[0]],
                            currentlyDisplayedCards[chosenCards[1]],
                            currentlyDisplayedCards[chosenCards[2]]
                    },
                    chosenCards.clone()
            ));
        }
    }

    private void EnsurePlayable() {
        if (gameIsOver) {
            return;
        }

        while (!isSetPresent()) {
            if (deck.isEmpty()) {
                gameIsOver = true;
                eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.GAME_END, numberOfSetsFound));
                return;
            } else {
                // Add more cards
                currentlyDisplayedCards[currentBoardSize] = deck.Draw();
                currentlyDisplayedCards[currentBoardSize + 1] = deck.Draw();
                currentlyDisplayedCards[currentBoardSize + 2] = deck.Draw();
                currentBoardSize += 3;
                // Trigger this event in case the view has to animate something.
                eventHandlerSet.triggerEvent(new EventInstance<SetGameEvent>(SetGameEvent.CARDS_ADDED,
                        new Object[] {
                            new SetCard[]{
                                currentlyDisplayedCards[currentBoardSize-3],
                                currentlyDisplayedCards[currentBoardSize-2],
                                currentlyDisplayedCards[currentBoardSize-1],
                            }
                }
                        ));
            }
        }
    }

    public long getSeed() {
        return seed;
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

    public void reset() {
        deck.shuffle();
        currentlyDisplayedCards = new SetCard[21];
        currentBoardSize = DEFAULT_BOARD_SIZE;
        numberOfSetsFound = 0;
        gameIsOver = false;
        eventHandlerSet.getHistory().clear();
        hint = new IntTriple();
    }
}
