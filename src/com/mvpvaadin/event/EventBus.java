package com.mvpvaadin.event;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

public class EventBus implements Serializable {
	
	private static final long serialVersionUID = 5500479291703928578L;
	
	private Map<EventType<? extends EventHandler>, Set<EventHandler>> handlerMap;
	
	public EventBus(){
		handlerMap = new LinkedHashMap<EventType<? extends EventHandler>, Set<EventHandler>>();
	}
	
	
	
	
	/**
	 * Registers an  {@link EventHandler}  for the specified {@link EventType}
	 * @param type
	 * @param handler
	 */
	public <H extends EventHandler> void addHandler(EventType<H> type, H handler){
		Set<EventHandler> handlersForType = handlerMap.get(type);
		
		if (handlersForType!=null)
			handlersForType.add(handler);
		else
		{
			handlersForType = new LinkedHashSet<EventHandler>();
			handlersForType.add(handler);
			handlerMap.put(type, handlersForType);
		}
	}
	
	
	/**
	 * Removes an {@link EventHandler} from getting Events of the specified {@link EventType}
	 * @param type
	 * @param handler
	 */
	public <H extends EventHandler> void removeHandler(EventType<H> type, H handler){
		Set<EventHandler> handlersForType = handlerMap.get(type);
		
		if (handlersForType!=null)
			handlersForType.remove(handler);
		
	}
	
	
	/**
	 * Removes the handler from every event registration
	 * @param handler
	 */
	public void removeHandler(EventHandler handler){
		
		for (Entry<EventType<? extends EventHandler>, Set<EventHandler>> e : handlerMap.entrySet()){
			e.getValue().remove(handler);
		}
		
	}
	
	
	/**
	 * Fires a Event to the EventBus to inform all registered EventHandlers about this Event
	 * @param event
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void fireEvent(Event event){
		
		Set<EventHandler> handlers = handlerMap.get(event.getType());
		
		if (handlers != null)
			for (EventHandler h : handlers)
				event.dispatch(h);
		
	}
	
	
	
}
