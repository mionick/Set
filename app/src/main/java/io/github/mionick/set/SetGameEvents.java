package io.github.mionick.set;

import java.lang.reflect.Type;

import io.github.mionick.events.IEventEnum;

// ============================ EVENT HOOKS =============================
    /*
    Events:
    // They don't have to return anything
    (Event) <Parameters...>
    CorrectSet <String, SetCard[]>
    IncorrectSet <String, SetCard[]>
    GameStart <long>
    GameEnd <long>
    NoSet <long>
     */
public enum SetGameEvents implements IEventEnum, Comparable<SetGameEvents> {
    GAME_START,
    // number of sets
    GAME_END(Integer.class),
    NO_SETS,
    CARDS_ADDED,
    // These take a timestamp, an identifier of the player who picked the set, and the cards selected.
    CORRECT_SET  (String.class, SetCard[].class),
    INCORRECT_SET(String.class, SetCard[].class),
    GAME_PAUSED,
    GAME_RESUMED
    ;

    private final Type[] params;

    SetGameEvents(Type... params) {
        this.params = params;
    }

    @Override
    public Type[] getParameterTypes() {
        return params;
    }



}