package io.github.mionick.events;

import android.support.annotation.NonNull;

/**
 * Created by Nick on 5/27/2018.
 */

public class EventInstance<T extends Enum & IEventEnum> implements Comparable<EventInstance>{

    private long timestamp;
    private T type;
    private Object[] params;

    public EventInstance(long timestamp, T type, Object... params) {
        this.timestamp = timestamp;
        this.type = type;
        this.params = params;
    }

    public EventInstance(T type, Object... params) {
        this.timestamp = System.nanoTime();
        this.type = type;
        this.params = params;
    }

    public long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public int compareTo(@NonNull EventInstance other) {
        return Long.compare(this.timestamp, other.getTimestamp());
    }

    public Object[] getParams() {
        return params;
    }

    public T getType() {
        return type;
    }
}
