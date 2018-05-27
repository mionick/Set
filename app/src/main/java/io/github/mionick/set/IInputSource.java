package io.github.mionick.set;

/**
 * Any input method for selecting cards must use this interface.
 * The controller will register a listener with the selected() event, and react accordingly.
 */
public interface IInputSource {
    void addSelectionEventHandler(ISelectCards handler);
}
