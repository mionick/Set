package io.github.mionick.set.web;

import java.lang.reflect.Type;

import io.github.mionick.events.IEventEnum;

/**
 * Created by Nick on 6/3/2018.
 */

public enum CommunicationEvent implements IEventEnum {
    PLAYER_JOINED(String.class),
    KICKED_OUT(),
    JOINED_SUCCESSFULLY(),
    NAME_TAKEN(),
    // Local IP, HotSpot IP
    COMMUNICATION(String.class, String.class),
    GAME_IN_PROGRESS();

    private Type[] params;

    CommunicationEvent(Type... params) {
        this.params = params;
    }

    @Override
    public Type[] getParameterTypes() {
        return params;
    }
}
