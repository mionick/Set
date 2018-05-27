package io.github.mionick.events;

public interface Action {
    void apply(Long timestamp, Object... params);
}
