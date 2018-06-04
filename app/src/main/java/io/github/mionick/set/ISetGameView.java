package io.github.mionick.set;

import io.github.mionick.events.EventInstance;

/**
 * This is the interface that any view of the game must implement.
 * It's essentially just guaranteeing they can handle the SetGameEvents, since that's all you need to display properly.
 *
 * Created by Nick on 5/28/2018.
 */

public interface ISetGameView {
    void OnGameEvent(EventInstance<SetGameEvent> event);
}
