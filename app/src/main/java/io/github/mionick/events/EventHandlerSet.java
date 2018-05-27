package io.github.mionick.events;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EventHandlerSet<T extends Enum & IEventEnum> {

    HashMap<T, ArrayList<Action>> handlers = new HashMap<>(10);

    public EventHandlerSet( Class<T> clazz) {

        // Initialize the list of handlers for each enum event
        for (T type : clazz.getEnumConstants()) {
            handlers.put(type, new ArrayList<>());
        }
    }

    public void AddHandler(T eventType, Action action) {
        handlers.get(eventType).add(action);
    }

    // params is expecting to be a list matching the params types of the event
    public void triggerEvent(EventInstance<T> event) {

        // Safety Check, the passed in objects should have the correct type.
        Type[] types = event.getType().getParameterTypes();
        for (int i = 0; i < types.length; i++) {
            if (event.getParams()[i].getClass() != types[i]) {
                throw new IllegalArgumentException("Expected: " + types[i] + " but Received: " + event.getParams()[i].getClass());
            }
        }

        ArrayList<Action> handlerList = handlers.get(event.getType());

        eventList.add(event);

        for (Action action : handlerList) {
            action.apply(event.getTimestamp(), event.getParams());
        }
    }
    // This is a history of the events this handler has received.
    private ArrayList<EventInstance<T>> eventList = new ArrayList<>();

    public List<EventInstance<T>> getHistory() {
        return eventList;
    }
}

