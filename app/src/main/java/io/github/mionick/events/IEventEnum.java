package io.github.mionick.events;

import java.lang.reflect.Type;

/**
 * Created by Nick on 5/27/2018.
 */

// This has to be a class so that it can be an interface so that the enums can use it.
public interface IEventEnum {
    Type[] getParameterTypes();
}

