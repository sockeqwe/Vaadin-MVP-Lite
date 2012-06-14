package com.mvpvaadin.event;

import java.io.Serializable;

public abstract class Event<T extends EventHandler> implements Serializable {
	
	private static final long serialVersionUID = -2059473765514136178L;

	public abstract EventType<T> getType();
	
	public abstract void dispatch(T handler);
	
}
