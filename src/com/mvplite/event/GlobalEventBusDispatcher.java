package com.mvplite.event;

/**
 * This is the common interface for Dispatchers, that dispatches
 * the Event from the {@link GlobalEventBus} to the local {@link EventBus}
 * @author Hannes Dorfmann
 *
 */
public interface GlobalEventBusDispatcher {
	
	/**
	 * Starts to listen to the {@link GlobalEventBus} 
	 * for new incomming {@link Event}s
	 */
	public void start();
	
	/**
	 * Stops to listen to the {@link GlobalEventBus}
	 */
	public void stop();

}
