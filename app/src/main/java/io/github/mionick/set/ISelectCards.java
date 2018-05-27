package io.github.mionick.set;

/**
 * The Input listeners must expose this for the controller.
 */

public interface ISelectCards {
    void SelectSet(String sourceName, IntTriple set);
}
