package com.mvpvaadin.event;

public abstract class Event<T extends EventHandler> {
	
	public abstract EventType<T> getType();
	
	public abstract void dispatch(T handler);
	
}
