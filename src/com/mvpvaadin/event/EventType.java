package com.mvpvaadin.event;

import java.io.Serializable;

/**
 * Every {@link Event} has its one {@link EventType}. But all instances of a
 * {@link Event} must have the same {@link EventType} object, which is returned by
 * {@link Event#getType()}. This is used by the {@link EventBus} to determine the registered
 * {@link EventHandler}s in constant time.
 * @author Hannes Dorfmann
 *
 * @param <T>
 */
public class EventType <T extends EventHandler> implements Serializable{

	private static final long serialVersionUID = -8411159259067122385L;
	

}
