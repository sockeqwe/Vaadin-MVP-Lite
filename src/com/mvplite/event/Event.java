package com.mvplite.event;

import java.io.Serializable;

/**
 * This is the super base type for every event, that can be fired to the {@link EventBus}
 * @author Hannes Dorfmann
 *
 * @param <T> the corresponding {@link EventHandler} that handles this event
 */
public abstract class Event<T extends EventHandler> implements Serializable {
	
	private static final long serialVersionUID = -2059473765514136178L;
	
	/**
	 * Every {@link Event} has one {@link EventType}, which is returned by this method.
	 * But all instances of all
	 * {@link Event}s of the same class must have the same {@link EventType} object.
	 * So the best way to implement a correct behaviour in your SubEvent that extends Event is,
	 * to make a static field {@link EventType} and return this static field with this method.
	 * This method is used by the {@link EventBus} to determine the registered
	 * {@link EventHandler}s in constant time.
	 */
	public abstract EventType<T> getType();
	
	
	/**
	 * This method is called by the {@link EventBus} to deliver / dispatch a fired {@link Event}.
	 * Here you must specify, which method of the corresponding {@link EventHandler} must be called
	 * to deliver / dispatch the Event to the {@link EventHandler}
	 * @param handler
	 */
	public abstract void dispatch(T handler);
	
}
