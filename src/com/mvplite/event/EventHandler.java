package com.mvplite.event;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;


/**
 * This is the base type for every {@link EventHandler} that handles {@link Event}s.
 * A {@link EventHandler} must be registered to the {@link EventBus} to receive {@link Event}s.
 * This is done by {@link EventBus#addHandler(EventType, EventHandler)}
 * @author Hannes Dorfmann
 *
 */
@Retention(value=RetentionPolicy.RUNTIME)
public @interface EventHandler {

}
